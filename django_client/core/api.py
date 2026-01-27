import requests
from django.conf import settings


def _headers(token):
    if not token:
        return {}
    return {"Authorization": f"Bearer {token}"}


def register(data):
    return requests.post(f"{settings.API_BASE_URL}/api/auth/register", json=data)


def login(data):
    return requests.post(f"{settings.API_BASE_URL}/api/auth/login", json=data)


def get_requests(token, params=None):
    return requests.get(f"{settings.API_BASE_URL}/api/requests", headers=_headers(token), params=params)


def get_open_requests(token, params=None):
    return requests.get(f"{settings.API_BASE_URL}/api/requests/open", headers=_headers(token), params=params)


def create_request(token, data):
    return requests.post(f"{settings.API_BASE_URL}/api/requests", headers=_headers(token), json=data)


def get_request(token, request_id):
    return requests.get(
        f"{settings.API_BASE_URL}/api/requests/{request_id}",
        headers=_headers(token),
    )


def get_request_applications(token, request_id, params=None):
    return requests.get(
        f"{settings.API_BASE_URL}/api/requests/{request_id}/applications",
        headers=_headers(token),
        params=params,
    )


def apply_to_request(token, request_id, data):
    return requests.post(
        f"{settings.API_BASE_URL}/api/requests/{request_id}/apply",
        headers=_headers(token),
        json=data,
    )


def accept_application(token, application_id):
    return requests.patch(
        f"{settings.API_BASE_URL}/api/applications/{application_id}/accept",
        headers=_headers(token),
    )


def reject_application(token, application_id):
    return requests.patch(
        f"{settings.API_BASE_URL}/api/applications/{application_id}/reject",
        headers=_headers(token),
    )


def withdraw_application(token, application_id):
    return requests.patch(
        f"{settings.API_BASE_URL}/api/applications/{application_id}/withdraw",
        headers=_headers(token),
    )


def my_applications(token, params=None):
    return requests.get(
        f"{settings.API_BASE_URL}/api/applications",
        headers=_headers(token),
        params=params,
    )


def get_users(token, params=None):
    return requests.get(
        f"{settings.API_BASE_URL}/api/users",
        headers=_headers(token),
        params=params,
    )

def get_admin_stats(token):
    return requests.get(
        f"{settings.API_BASE_URL}/api/admin/stats",
        headers=_headers(token),
    )


def update_user_role(token, user_id, role):
    return requests.put(
        f"{settings.API_BASE_URL}/api/users/{user_id}/role",
        headers=_headers(token),
        json={"role": role},
    )


def update_user_status(token, user_id, active):
    return requests.put(
        f"{settings.API_BASE_URL}/api/users/{user_id}/status",
        headers=_headers(token),
        json={"active": active},
    )


def get_profile(token, user_id):
    return requests.get(
        f"{settings.API_BASE_URL}/api/users/{user_id}/profile",
        headers=_headers(token),
    )


def update_profile(token, user_id, data):
    return requests.put(
        f"{settings.API_BASE_URL}/api/users/{user_id}/profile",
        headers=_headers(token),
        json=data,
    )

def upload_avatar(token, file_obj):
    return requests.post(
        f"{settings.API_BASE_URL}/api/files/avatar",
        headers=_headers(token),
        files={"file": (file_obj.name, file_obj, file_obj.content_type)},
    )

def upload_request_image(token, file_obj):
    return requests.post(
        f"{settings.API_BASE_URL}/api/files/request",
        headers=_headers(token),
        files={"file": (file_obj.name, file_obj, file_obj.content_type)},
    )

def upload_blog_image(token, file_tuple):
    # file_tuple: (filename, fileobj, content_type)
    return requests.post(
        f"{settings.API_BASE_URL}/api/files/blog",
        headers=_headers(token),
        files={"file": file_tuple},
    )


def complete_request(token, request_id):
    return requests.patch(
        f"{settings.API_BASE_URL}/api/requests/{request_id}/complete",
        headers=_headers(token),
    )


def cancel_request(token, request_id):
    return requests.patch(
        f"{settings.API_BASE_URL}/api/requests/{request_id}/cancel",
        headers=_headers(token),
    )


def create_review(token, request_id, data):
    return requests.post(
        f"{settings.API_BASE_URL}/api/requests/{request_id}/reviews",
        headers=_headers(token),
        json=data,
    )


def get_reviews(token, user_id, params=None):
    return requests.get(
        f"{settings.API_BASE_URL}/api/users/{user_id}/reviews",
        headers=_headers(token),
        params=params,
    )


def get_rewards(token):
    return requests.get(
        f"{settings.API_BASE_URL}/api/rewards/me",
        headers=_headers(token),
    )


def get_leaderboard(params=None):
    return requests.get(
        f"{settings.API_BASE_URL}/api/rewards/leaderboard",
        params=params,
    )


def get_blogs(params=None):
    try:
        return requests.get(
            f"{settings.API_BASE_URL}/api/blogs",
            params=params,
            timeout=3,
        )
    except Exception:
        return None


def get_blog(slug):
    try:
        return requests.get(
            f"{settings.API_BASE_URL}/api/blogs/{slug}",
            timeout=3,
        )
    except Exception:
        return None


def get_admin_blogs(token, params=None):
    return requests.get(
        f"{settings.API_BASE_URL}/api/admin/blogs",
        headers=_headers(token),
        params=params,
    )


def create_blog(token, data):
    return requests.post(
        f"{settings.API_BASE_URL}/api/admin/blogs",
        headers=_headers(token),
        json=data,
    )


def update_blog(token, blog_id, data):
    return requests.put(
        f"{settings.API_BASE_URL}/api/admin/blogs/{blog_id}",
        headers=_headers(token),
        json=data,
    )


def delete_blog(token, blog_id):
    return requests.delete(
        f"{settings.API_BASE_URL}/api/admin/blogs/{blog_id}",
        headers=_headers(token),
    )


def get_request_messages(token, request_id, params=None):
    return requests.get(
        f"{settings.API_BASE_URL}/api/requests/{request_id}/messages",
        headers=_headers(token),
        params=params,
    )


def send_request_message(token, request_id, data):
    return requests.post(
        f"{settings.API_BASE_URL}/api/requests/{request_id}/messages",
        headers=_headers(token),
        json=data,
    )
