from django.shortcuts import render, redirect
from django.contrib import messages
from django.views.decorators.http import require_http_methods
from django.conf import settings
from urllib.parse import urlparse

from . import api
from .forms import LoginForm, RegisterForm, RequestForm, ApplicationForm, ProfileForm, ReviewForm, BlogForm


def _safe_message(response, default_msg):
    try:
        return response.json().get("message", default_msg)
    except Exception:
        return default_msg


def _safe_json(response, default=None):
    if response is None:
        return {} if default is None else default
    try:
        return response.json()
    except Exception:
        return {} if default is None else default


def _full_media_url(url, request=None):
    if not url:
        return url
    parsed = urlparse(url)
    # If absolute but points to frontend host (common when URLs were stored before fix), rewrite to API base.
    api_base = (settings.API_BASE_URL or "http://localhost:8080").strip().rstrip("/")
    if parsed.scheme in ("http", "https"):
        if "/uploads/" in parsed.path:
            # rebuild using API base and the original path
            return api_base + parsed.path
        return url  # leave other absolutes untouched
    # relative path
    if url.startswith("//"):
        return "http:" + url
    return api_base + url


def _get_token(request):
    return request.session.get("token")


def _is_admin(request):
    user = request.session.get("user") or {}
    return user.get("role") == "ADMIN"


def home(request):
    blogs_response = api.get_blogs(params={"page": 0, "size": 3})
    stats_resp = api.get_public_stats()
    stats = _safe_json(stats_resp, {}) if stats_resp is not None and stats_resp.status_code == 200 else {}
    posts = []
    if blogs_response is not None and blogs_response.status_code == 200:
        try:
            payload = _safe_json(blogs_response, {})
            posts = payload.get("content", [])
            for p in posts:
                p["coverImageUrl"] = _full_media_url(p.get("coverImageUrl"), request)
                if p.get("gallery"):
                    p["gallery"] = [_full_media_url(g, request) for g in p.get("gallery", [])]
        except Exception:
            posts = []
    return render(request, "landing.html", {"posts": posts, "stats": stats})


def contact(request):
    return render(request, "contact.html")


def help_page(request):
    return render(request, "help.html")


def faq(request):
    return render(request, "faq.html")


def feedback(request):
    return render(request, "feedback.html")


def privacy(request):
    return render(request, "privacy.html")


def terms(request):
    return render(request, "terms.html")


@require_http_methods(["GET", "POST"])
def login_view(request):
    form = LoginForm(request.POST or None)
    if request.method == "POST" and form.is_valid():
        response = api.login(form.cleaned_data)
        if response.status_code == 200:
            payload = _safe_json(response, {})
            request.session["token"] = payload.get("token")
            request.session["user"] = payload.get("user")
            return redirect("dashboard")
        messages.error(request, _safe_message(response, "Hyrja dështoi"))
    return render(request, "login.html", {"form": form})


@require_http_methods(["GET", "POST"])
def register_view(request):
    form = RegisterForm(request.POST or None)
    if request.method == "POST" and form.is_valid():
        response = api.register(form.cleaned_data)
        if response.status_code in (200, 201):
            payload = _safe_json(response, {})
            request.session["token"] = payload.get("token")
            request.session["user"] = payload.get("user")
            return redirect("dashboard")
        messages.error(request, _safe_message(response, "Regjistrimi dështoi"))
    return render(request, "register.html", {"form": form})


def logout_view(request):
    request.session.flush()
    return redirect("login")


def dashboard(request):
    token = _get_token(request)
    if not token:
        return redirect("login")
    rewards = {}
    leaderboard = []
    notifications = []
    user = request.session.get("user") or {}

    rewards_resp = api.get_rewards(token)
    if rewards_resp.status_code == 200:
        rewards = _safe_json(rewards_resp, {})
    lb_resp = api.get_leaderboard(params={"page": 0, "size": 5})
    if lb_resp.status_code == 200:
        leaderboard = _safe_json(lb_resp, {}).get("content", [])
    notif_resp = api.get_notifications(token, params={"page": 0, "size": 8})
    if notif_resp.status_code == 200:
        notifications = _safe_json(notif_resp, {}).get("content", [])
    return render(
        request,
        "dashboard.html",
        {
            "user": request.session.get("user"),
            "rewards": rewards,
            "leaderboard": leaderboard,
            "notifications": notifications[:8],
        },
    )


@require_http_methods(["GET"])
def notifications_view(request):
    token = _get_token(request)
    if not token:
        return redirect("login")
    page = request.GET.get("page", 0)
    resp = api.get_notifications(token, params={"page": page, "size": 50})
    data = _safe_json(resp, {"content": []}) if resp.status_code == 200 else {"content": []}
    return render(request, "notifications.html", {"notifications": data.get("content", [])})


@require_http_methods(["GET", "POST"])
def profile(request):
    token = _get_token(request)
    if not token:
        return redirect("login")
    user = request.session.get("user") or {}
    user_id = user.get("id")
    if not user_id:
        messages.error(request, "Seanca e përdoruesit mungon")
        return redirect("dashboard")
    form = ProfileForm(request.POST or None, request.FILES or None)
    if request.method == "POST" and form.is_valid():
        if request.FILES.get("avatar_file"):
            upload = api.upload_avatar(token, request.FILES["avatar_file"])
            if upload.status_code in (200, 201):
                avatar_url = _safe_json(upload, {}).get("url")
                form.cleaned_data["avatarUrl"] = avatar_url
            else:
                messages.error(request, "Ngarkimi i fotos dështoi")
        response = api.update_profile(token, user_id, form.cleaned_data)
        if response.status_code == 200:
            messages.success(request, "Profili u përditësua")
        else:
            messages.error(request, _safe_message(response, "Përditësimi dështoi"))
    response = api.get_profile(token, user_id)
    profile_data = _safe_json(response, {}) if response.status_code == 200 else {}
    if profile_data.get("avatarUrl"):
        profile_data["avatarUrl"] = _full_media_url(profile_data.get("avatarUrl"), request)
    form_initial = {
        "bio": profile_data.get("bio") or "",
        "avatarUrl": profile_data.get("avatarUrl") or "",
        "location": profile_data.get("location") or "",
        "phone": profile_data.get("phone") or "",
        "skills": profile_data.get("skills") or "",
        "availability": profile_data.get("availability") or "",
        "profilePublic": profile_data.get("profilePublic", True),
    }
    form = ProfileForm(initial=form_initial)
    skills_list = [s.strip() for s in (profile_data.get("skills") or "").split(",") if s.strip()]
    return render(request, "profile.html", {"form": form, "profile": profile_data, "skills_list": skills_list})


@require_http_methods(["GET", "POST"])
def requests_list(request):
    token = _get_token(request)
    if not token:
        return redirect("login")

    form = RequestForm(request.POST or None)
    if request.method == "POST" and form.is_valid():
        response = api.create_request(token, form.cleaned_data)
        if response.status_code in (200, 201):
            messages.success(request, "Thirrja u krijua")
            return redirect("requests")
        messages.error(request, _safe_message(response, "Krijimi dështoi"))

    q = request.GET.get("q")
    tab = request.GET.get("tab", "all")
    params = {"page": 0, "size": 20}
    if q:
        params["q"] = q
    counts_params = {"page": 0, "size": 1}
    if q:
        counts_params["q"] = q
    all_resp = api.get_requests(token, params=counts_params)
    open_resp = api.get_open_requests(token, params=counts_params)
    all_count = _safe_json(all_resp, {}).get("totalElements", 0) if all_resp.status_code == 200 else 0
    open_count = _safe_json(open_resp, {}).get("totalElements", 0) if open_resp.status_code == 200 else 0
    if tab == "open":
        response = api.get_open_requests(token, params=params)
    else:
        response = api.get_requests(token, params=params)
    data = _safe_json(response, {"content": []}) if response.status_code == 200 else {"content": []}
    requests_list = data.get("content", [])
    for item in requests_list:
        item["imageUrl"] = _full_media_url(item.get("imageUrl"), request)
    if tab == "mine":
        user_id = (request.session.get("user") or {}).get("id")
        if user_id:
            requests_list = [r for r in requests_list if r.get("ownerId") == user_id]
        else:
            requests_list = []
    mine_count = len(requests_list) if tab == "mine" else None
    return render(
        request,
        "requests.html",
        {
            "requests": requests_list,
            "form": form,
            "q": q,
            "tab": tab,
            "all_count": all_count,
            "open_count": open_count,
            "mine_count": mine_count,
        },
    )


@require_http_methods(["GET", "POST"])
def request_detail(request, request_id):
    token = _get_token(request)
    if not token:
        return redirect("login")

    user = request.session.get("user") or {}
    user_id = user.get("id")

    request_data = {}
    req_resp = api.get_request(token, request_id)
    if req_resp.status_code == 200:
        request_data = _safe_json(req_resp, {})
        request_data["imageUrl"] = _full_media_url(request_data.get("imageUrl"), request)
    else:
        messages.error(request, _safe_message(req_resp, "Thirrja nuk u gjet"))
        return redirect("requests")

    apply_form = ApplicationForm(request.POST or None)
    review_form = ReviewForm(request.POST or None)
    messages_list = []

    accepted_volunteer_id = request_data.get("acceptedVolunteerId")
    owner_id = request_data.get("ownerId")
    status = request_data.get("status")
    reviewee_id = None
    can_chat = False
    if user_id and (user_id == owner_id or user_id == accepted_volunteer_id):
        can_chat = True
    if status == "COMPLETED":
        if user_id == owner_id:
            reviewee_id = accepted_volunteer_id
        elif user_id == accepted_volunteer_id:
            reviewee_id = owner_id

    if request.method == "POST":
        form_type = request.POST.get("form_type", "apply")
        if form_type == "review" and review_form.is_valid() and reviewee_id:
            payload = review_form.cleaned_data | {"revieweeId": reviewee_id}
            response = api.create_review(token, request_id, payload)
            if response.status_code in (200, 201):
                messages.success(request, "Vlerësimi u regjistrua")
                return redirect("request_detail", request_id=request_id)
            messages.error(request, _safe_message(response, "Dështoi dërgimi i vlerësimit"))
        elif form_type == "apply" and apply_form.is_valid():
            response = api.apply_to_request(token, request_id, apply_form.cleaned_data)
            if response.status_code in (200, 201):
                messages.success(request, "Aplikimi u dërgua")
                return redirect("request_detail", request_id=request_id)
            messages.error(request, _safe_message(response, "Aplikimi dështoi"))
        elif form_type == "chat" and can_chat:
            body = request.POST.get("chat_body", "")
            if body.strip():
                resp = api.send_request_message(token, request_id, {"body": body})
                if resp and resp.status_code in (200, 201):
                    messages.success(request, "Mesazhi u dërgua")
                    return redirect("request_detail", request_id=request_id)
                else:
                    try:
                        messages.error(request, _safe_message(resp, "Mesazhi dështoi"))
                    except Exception:
                        messages.error(request, "Mesazhi dështoi")

    apps_response = api.get_request_applications(token, request_id, params={"page": 0, "size": 20})
    apps_data = _safe_json(apps_response, {"content": []}) if apps_response.status_code == 200 else {"content": []}
    applications = apps_data.get("content", [])
    for item in applications:
        if "id" not in item:
            item["id"] = item.get("applicationId")

    volunteer_reviews = []
    if accepted_volunteer_id:
        reviews_resp = api.get_reviews(token, accepted_volunteer_id, params={"page": 0, "size": 3})
        if reviews_resp.status_code == 200:
            volunteer_reviews = _safe_json(reviews_resp, {}).get("content", [])

    if can_chat:
        msgs_resp = api.get_request_messages(token, request_id, params={"page": 0, "size": 50})
        if msgs_resp and msgs_resp.status_code == 200:
            messages_list = _safe_json(msgs_resp, {}).get("content", [])

    return render(
        request,
        "request_detail.html",
        {
            "request_id": request_id,
            "request_data": request_data,
            "applications": applications,
            "form": apply_form,
            "review_form": review_form,
            "can_review": bool(reviewee_id),
            "volunteer_reviews": volunteer_reviews,
            "can_chat": can_chat,
            "chat_messages": messages_list,
        },
    )


def my_applications(request):
    token = _get_token(request)
    if not token:
        return redirect("login")
    response = api.my_applications(token, params={"page": 0, "size": 20})
    data = _safe_json(response, {"content": []}) if response.status_code == 200 else {"content": []}
    applications = data.get("content", [])
    for item in applications:
        if "id" not in item:
            item["id"] = item.get("applicationId")
    return render(request, "my_applications.html", {"applications": applications})


def admin_dashboard(request):
    token = _get_token(request)
    if not token:
        return redirect("login")
    if not _is_admin(request):
        messages.error(request, "Kërkohet akses admin")
        return redirect("dashboard")
    users_response = api.get_users(token)
    users_data = _safe_json(users_response, []) if users_response.status_code == 200 else []
    stats_response = api.get_admin_stats(token)
    stats_data = _safe_json(stats_response, {}) if stats_response.status_code == 200 else {}
    requests_response = api.get_requests(token, params={"page": 0, "size": 20})
    requests_data = _safe_json(requests_response, {"content": []}) if requests_response.status_code == 200 else {"content": []}
    blogs_response = api.get_admin_blogs(token, params={"page": 0, "size": 20})
    blogs_data = _safe_json(blogs_response, {"content": []}) if blogs_response.status_code == 200 else {"content": []}
    blog_form = BlogForm()
    blogs_list = blogs_data.get("content", [])
    for p in blogs_list:
        p["coverImageUrl"] = _full_media_url(p.get("coverImageUrl"), request)
        if p.get("gallery"):
            p["gallery"] = [_full_media_url(g, request) for g in p.get("gallery", [])]
    return render(
        request,
        "admin.html",
        {
            "users": users_data,
            "stats": stats_data,
            "requests": requests_data.get("content", []),
            "blogs": blogs_list,
            "blog_form": blog_form,
            "token": token,
            "api_base": settings.API_BASE_URL,
        },
    )


@require_http_methods(["POST"])
def update_role(request, user_id):
    token = _get_token(request)
    if not token:
        return redirect("login")
    if not _is_admin(request):
        messages.error(request, "Kërkohet akses admin")
        return redirect("dashboard")
    role = request.POST.get("role", "").upper()
    response = api.update_user_role(token, user_id, role)
    if response.status_code == 200:
        messages.success(request, "Role updated")
    else:
        messages.error(request, _safe_message(response, "Përditësimi dështoi"))
    return redirect("admin_dashboard")

# Blog mock data (static)
BLOG_POSTS = [
    {"id": 1, "title": "Si të menaxhosh vullnetarët", "author": "Stafi Vullnet", "date": "2026-01-01", "excerpt": "Këshilla praktike për krijuesit e thirrjeve.", "content": "Këshilla praktike për krijuesit e thirrjeve..."},
    {"id": 2, "title": "Historitë e suksesit", "author": "Stafi Vullnet", "date": "2026-01-05", "excerpt": "Shembuj nga komuniteti ynë.", "content": "Shembuj nga komuniteti ynë..."},
    {"id": 3, "title": "Siguria në terren", "author": "Stafi Vullnet", "date": "2026-01-10", "excerpt": "Si të ruash sigurinë gjatë ndihmës.", "content": "Si të ruash sigurinë gjatë ndihmës..."},
]


def blog_list(request):
    response = api.get_blogs(params={"page": 0, "size": 20})
    posts = []
    if response is not None and response.status_code == 200:
        posts = _safe_json(response, {}).get("content", [])
        for p in posts:
            p["coverImageUrl"] = _full_media_url(p.get("coverImageUrl"), request)
            if p.get("gallery"):
                p["gallery"] = [_full_media_url(g, request) for g in p.get("gallery", [])]
    else:
        posts = BLOG_POSTS
    return render(request, "blog_list.html", {"posts": posts})


def blog_detail(request, post_id):
    response = api.get_blog(post_id)
    if response is not None and response.status_code == 200:
        post = _safe_json(response, {})
        post["coverImageUrl"] = _full_media_url(post.get("coverImageUrl"), request)
        if post.get("gallery"):
            post["gallery"] = [_full_media_url(g, request) for g in post.get("gallery", [])]
        return render(request, "blog_detail.html", {"post": post})
    fallback_id = None
    try:
        fallback_id = int(post_id)
    except ValueError:
        fallback_id = None
    post = next((p for p in BLOG_POSTS if fallback_id and p["id"] == fallback_id), None)
    if not post:
        messages.error(request, "Artikulli nuk u gjet")
        return redirect("blog_list")
    return render(request, "blog_detail.html", {"post": post})


def about(request):
    return render(request, "about.html")


def profile_public(request, user_id):
    token = _get_token(request)
    response = api.get_profile(token, user_id)
    if response.status_code == 200:
        profile_data = _safe_json(response, {})
        if profile_data.get("avatarUrl"):
            profile_data["avatarUrl"] = _full_media_url(profile_data.get("avatarUrl"), request)
        skills_list = [s.strip() for s in (profile_data.get("skills") or "").split(",") if s.strip()]
        return render(request, "profile_public.html", {"profile": profile_data, "skills_list": skills_list})
    if response.status_code == 403:
        messages.error(request, "Profili është privat.")
    else:
        messages.error(request, _safe_message(response, "Profili nuk u gjet."))
    return redirect("/requests/?tab=open#messages")


@require_http_methods(["POST"])
def update_status(request, user_id):
    token = _get_token(request)
    if not token:
        return redirect("login")
    if not _is_admin(request):
        messages.error(request, "Kërkohet akses admin")
        return redirect("dashboard")
    active = request.POST.get("active") == "true"
    response = api.update_user_status(token, user_id, active)
    if response.status_code == 200:
        messages.success(request, "Status updated")
    else:
        messages.error(request, _safe_message(response, "Përditësimi dështoi"))
    return redirect("admin_dashboard")

@require_http_methods(["POST"])
def admin_delete_request(request, request_id):
    token = _get_token(request)
    if not token:
        return redirect("login")
    if not _is_admin(request):
        messages.error(request, "Kërkohet akses admin")
        return redirect("dashboard")
    resp = api.delete_request(token, request_id)
    if resp.status_code in (200, 204):
        messages.success(request, "Thirrja u fshi")
    else:
        messages.error(request, _safe_message(resp, "Fshirja dështoi"))
    return redirect("admin_dashboard")

@require_http_methods(["POST"])
def admin_broadcast_notification(request):
    token = _get_token(request)
    if not token:
        return redirect("login")
    if not _is_admin(request):
        messages.error(request, "Kërkohet akses admin")
        return redirect("dashboard")
    payload = {
        "type": request.POST.get("type", "SYSTEM"),
        "title": request.POST.get("title", ""),
        "body": request.POST.get("body", ""),
        "link": request.POST.get("link", ""),
    }
    resp = api.broadcast_notification(token, payload)
    if resp.status_code in (200, 201):
        messages.success(request, "Njoftimi u dërgua")
    else:
        messages.error(request, _safe_message(resp, "Dërgimi dështoi"))
    return redirect("admin_dashboard")


@require_http_methods(["POST"])
def accept_application(request, application_id):
    token = _get_token(request)
    if not token:
        return redirect("login")
    response = api.accept_application(token, application_id)
    if response.status_code == 200:
        messages.success(request, "Aplikimi u pranua")
    else:
        messages.error(request, _safe_message(response, "Pranimi dështoi"))
    return redirect(request.META.get("HTTP_REFERER", "requests"))


@require_http_methods(["POST"])
def reject_application(request, application_id):
    token = _get_token(request)
    if not token:
        return redirect("login")
    response = api.reject_application(token, application_id)
    if response.status_code == 200:
        messages.success(request, "Aplikimi u refuzua")
    else:
        messages.error(request, _safe_message(response, "Refuzimi dështoi"))
    return redirect(request.META.get("HTTP_REFERER", "requests"))


@require_http_methods(["POST"])
def withdraw_application(request, application_id):
    token = _get_token(request)
    if not token:
        return redirect("login")
    response = api.withdraw_application(token, application_id)
    if response.status_code == 200:
        messages.success(request, "Aplikimi u tërhoq")
    else:
        messages.error(request, _safe_message(response, "Tërheqja dështoi"))
    return redirect(request.META.get("HTTP_REFERER", "my_applications"))


@require_http_methods(["POST"])
def complete_request(request, request_id):
    token = _get_token(request)
    if not token:
        return redirect("login")
    response = api.complete_request(token, request_id)
    if response.status_code == 200:
        messages.success(request, "Thirrja u përfundua")
    else:
        messages.error(request, _safe_message(response, "Përfundimi dështoi"))
    return redirect(request.META.get("HTTP_REFERER", "requests"))


@require_http_methods(["POST"])
def cancel_request(request, request_id):
    token = _get_token(request)
    if not token:
        return redirect("login")
    response = api.cancel_request(token, request_id)
    if response.status_code == 200:
        messages.success(request, "Thirrja u anulua")
    else:
        messages.error(request, _safe_message(response, "Anulimi dështoi"))
    return redirect(request.META.get("HTTP_REFERER", "requests"))


@require_http_methods(["POST"])
def create_blog(request):
    token = _get_token(request)
    if not token:
        return redirect("login")
    if not _is_admin(request):
        messages.error(request, "Kërkohet admin")
        return redirect("dashboard")
    form = BlogForm(request.POST or None)
    if form.is_valid():
        payload = form.cleaned_data
        payload["published"] = bool(payload.get("published"))
        if payload.get("gallery"):
            payload["gallery"] = [line.strip() for line in payload.get("gallery").splitlines() if line.strip()]
        else:
            payload.pop("gallery", None)
        response = api.create_blog(token, payload)
        if response.status_code in (200, 201):
            messages.success(request, "Artikulli u shtua")
        else:
            messages.error(request, _safe_message(response, "Dështoi shtimi i artikullit"))
    else:
        messages.error(request, "Formulari i artikullit nuk është i plotë")
    return redirect("admin_dashboard")


@require_http_methods(["POST"])
def update_blog(request, blog_id):
    token = _get_token(request)
    if not token:
        return redirect("login")
    if not _is_admin(request):
        messages.error(request, "Kërkohet admin")
        return redirect("dashboard")
    form = BlogForm(request.POST or None)
    if form.is_valid():
        payload = form.cleaned_data
        payload["published"] = bool(payload.get("published"))
        if payload.get("gallery"):
            payload["gallery"] = [line.strip() for line in payload.get("gallery").splitlines() if line.strip()]
        else:
            payload.pop("gallery", None)
        response = api.update_blog(token, blog_id, payload)
        if response.status_code in (200, 201):
            messages.success(request, "Artikulli u përditësua")
        else:
            messages.error(request, _safe_message(response, "Dështoi përditësimi"))
    else:
        messages.error(request, "Formulari i artikullit nuk është i plotë")
    return redirect("admin_dashboard")


@require_http_methods(["POST"])
def delete_blog(request, blog_id):
    token = _get_token(request)
    if not token:
        return redirect("login")
    if not _is_admin(request):
        messages.error(request, "Kërkohet admin")
        return redirect("dashboard")
    response = api.delete_blog(token, blog_id)
    if response.status_code in (200, 204):
        messages.success(request, "Artikulli u fshi")
    else:
        messages.error(request, _safe_message(response, "Dështoi fshirja"))
    return redirect("admin_dashboard")
