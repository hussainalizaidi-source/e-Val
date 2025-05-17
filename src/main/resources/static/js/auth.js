document.addEventListener('DOMContentLoaded', () => {
    // Login Form Handler
    if (document.getElementById('loginForm')) {
        document.getElementById('loginForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value.trim();
            const password = document.getElementById('password').value;

            if (!email || !password) {
                showError('Please enter both email and password.');
                return;
            }

            try {
                console.log('Sending login request to http://localhost:8080/auth/login');
                const response = await fetch('http://localhost:8080/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ email, password })
                });

                console.log('Login response status:', response.status);
                console.log('Login response headers:', [...response.headers.entries()]);
                if (!response.ok) {
                    const errorText = await response.text();
                    console.log('Login error response:', errorText);
                    throw new Error(errorText || 'Login failed');
                }

                const data = await response.json();
                const token = response.headers.get('Authorization')?.replace('Bearer ', '') || data.token;

                if (!token) {
                    throw new Error('No token received');
                }

                console.log('Token received:', token);
                console.log('Token parts:', token.split('.'));
                // Store token in localStorage as jwtToken
                localStorage.setItem('jwtToken', token);

                // Redirect based on role
                console.log('Role:', data.role);
                switch (data.role?.toUpperCase()) {
                    case 'ADMIN':
                        window.location.href = '/institution.html';
                        break;
                    case 'TEACHER':
                        window.location.href = '/teacherdash.html';
                        break;
                    case 'STUDENT':
                        window.location.href = '/studentdash.html';
                        break;
                    default:
                        window.location.href = '/';
                }
            } catch (error) {
                console.error('Login error:', error.message);
                showError(error.message || 'Network error - please try again');
            }
        });
    }

    // Registration Form Handler
    if (document.getElementById('registerForm')) {
        document.getElementById('registerForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const fullName = document.getElementById('fullName').value.trim();
            const email = document.getElementById('regEmail').value.trim();
            const password = document.getElementById('regPassword').value;
            const role = document.querySelector('input[name="role"]:checked')?.value;

            if (!fullName || !email || !password || !role) {
                showError('Please fill in all fields.');
                return;
            }

            try {
                console.log('Sending register request to http://localhost:8080/auth/register');
                const response = await fetch('http://localhost:8080/auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ 
                        name: fullName, 
                        email, 
                        password,
                        role
                    })
                });

                console.log('Register response status:', response.status);
                if (!response.ok) {
                    const errorText = await response.text();
                    console.log('Register error response:', errorText);
                    throw new Error(errorText || 'Registration failed');
                }

                alert('Registration successful! Please login.');
                window.location.href = '/login.html';
            } catch (error) {
                console.error('Register error:', error.message);
                showError(error.message || 'Network error - please try again');
            }
        });
    }

    function showError(message) {
        const errorDiv = document.getElementById('errorMessage');
        errorDiv.textContent = message;
        errorDiv.style.display = 'block';
        setTimeout(() => {
            errorDiv.textContent = '';
            errorDiv.style.display = 'none';
        }, 5000);
    }
});