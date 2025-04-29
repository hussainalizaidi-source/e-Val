document.addEventListener('DOMContentLoaded', () => {
    // Login Form Handler
    if(document.getElementById('loginForm')) {
        document.getElementById('loginForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            try {
                const response = await fetch('/auth/login', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ email, password })
                });

                const data = await response.json();
                
                if(response.ok) {
                    localStorage.setItem('token', data.token);
    
                    // Get role from token and redirect
                    const token = data.token;
                    const payload = JSON.parse(atob(token.split('.')[1]));
                    switch(payload.role) {
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
                } else {
                    showError(data.message || 'Login failed');
                }
            } catch (error) {
                showError('Network error - please try again');
            }
        });
    }

    // Registration Form Handler
    if(document.getElementById('registerForm')) {
        document.getElementById('registerForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            const fullName = document.getElementById('fullName').value;
            const email = document.getElementById('regEmail').value;
            const password = document.getElementById('regPassword').value;
            const role = document.querySelector('input[name="role"]:checked').value;
        
            try {
                const response = await fetch('/auth/register', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify({ 
                        name: fullName, 
                        email, 
                        password,
                        role  // Add role to request body
                    })
                });
        
                const data = await response.json();
                
                if(response.ok) {
                    alert('Registration successful! Please login.');
                    window.location.href = '/login.html';
                } else {
                    showError(data.message || 'Registration failed');
                }
            } catch (error) {
                showError('Network error - please try again');
            }
        });
    }

    function showError(message) {
        const errorDiv = document.getElementById('errorMessage');
        errorDiv.textContent = message;
        setTimeout(() => errorDiv.textContent = '', 5000);
    }
});