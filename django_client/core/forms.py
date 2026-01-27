from django import forms


class LoginForm(forms.Form):
    email = forms.EmailField(label="Email")
    password = forms.CharField(widget=forms.PasswordInput(), label="Fjalëkalimi")


class RegisterForm(forms.Form):
    firstName = forms.CharField(label="Emri")
    lastName = forms.CharField(label="Mbiemri")
    email = forms.EmailField(label="Email")
    phone = forms.CharField(label="Telefoni")
    password = forms.CharField(widget=forms.PasswordInput(), label="Fjalëkalimi")


class RequestForm(forms.Form):
    title = forms.CharField(label="Titulli")
    description = forms.CharField(widget=forms.Textarea, label="Përshkrimi")
    location = forms.CharField(label="Vendndodhja")
    imageUrl = forms.CharField(required=False, widget=forms.HiddenInput())


class ApplicationForm(forms.Form):
    message = forms.CharField(widget=forms.Textarea, label="Mesazhi")


class ProfileForm(forms.Form):
    bio = forms.CharField(widget=forms.Textarea, required=False, label="Bio")
    avatarUrl = forms.CharField(required=False, label="Link i fotos (opsional)")
    location = forms.CharField(required=False, label="Vendndodhja")
    phone = forms.CharField(required=False, label="Telefon")
    skills = forms.CharField(widget=forms.Textarea, required=False, label="Aftësitë")
    availability = forms.BooleanField(required=False, label="Në dispozicion")
    profilePublic = forms.BooleanField(required=False, label="Profili publik")


class ReviewForm(forms.Form):
    rating = forms.IntegerField(min_value=1, max_value=5, label="Vlerësimi (1-5)")
    comment = forms.CharField(widget=forms.Textarea, required=False, label="Komenti")


class BlogForm(forms.Form):
    title = forms.CharField(label="Titulli")
    summary = forms.CharField(widget=forms.Textarea, required=False, label="Përmbledhja")
    content = forms.CharField(widget=forms.Textarea, label="Përmbajtja")
    coverImageUrl = forms.CharField(required=False, label="Link i imazhit")
    published = forms.BooleanField(required=False, initial=True, label="Publikuar")
    gallery = forms.CharField(widget=forms.Textarea, required=False, label="Galeria (URL të ndara me rresht)")
