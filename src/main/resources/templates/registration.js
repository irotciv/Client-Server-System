const firstName = document.getElementById('first-name');
const lastName = document.getElementById('last-name');
const password = document.getElementById('password');
const email = document.getElementById('email');
const registerButton = document.getElementById('register');

registerButton.addEventListener('click', function (e) {
    e.preventDefault();
    const emailRegex = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.com$/;
    if (firstName.value !== '' && lastName.value !== '' && password.value !== '' && email.value !== '') {
        emailRegex.test(email.value);
    } else {
        document.querySelector('.error').innerHTML = 'Please, check if the data are correct.'
    }
});