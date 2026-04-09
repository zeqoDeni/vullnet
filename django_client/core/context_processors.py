from django.conf import settings


def api_base_url(request):
    return {"api_base_url": settings.API_BASE_URL}
