document.addEventListener('DOMContentLoaded', () => {
    // ==========================================================================
    // 1. MOUSE FOLLOW RADIAL GLOW TRACKER
    // ==========================================================================
    document.body.addEventListener('mousemove', (e) => {
        const x = e.clientX;
        const y = e.clientY;
        document.body.style.setProperty('--mouse-x', `${x}px`);
        document.body.style.setProperty('--mouse-y', `${y}px`);
    });

    // ==========================================================================
    // 2. INTERACTIVE PARTICLES SYSTEM (CANVAS DRIVEN)
    // ==========================================================================
    const canvas = document.getElementById('particles-canvas');
    if (canvas) {
        const ctx = canvas.getContext('2d');
        let particlesArray = [];
        
        function resizeCanvas() {
            canvas.width = window.innerWidth;
            canvas.height = window.innerHeight;
        }
        window.addEventListener('resize', resizeCanvas);
        resizeCanvas();

        class Particle {
            constructor() {
                this.x = Math.random() * canvas.width;
                this.y = Math.random() * canvas.height;
                this.size = Math.random() * 2 + 0.5;
                this.speedX = Math.random() * 0.3 - 0.15;
                this.speedY = Math.random() * 0.4 - 0.2;
                this.color = Math.random() > 0.5 ? 'rgba(6, 182, 212, 0.4)' : 'rgba(168, 85, 247, 0.4)';
            }
            update() {
                this.x += this.speedX;
                this.y += this.speedY;

                if (this.x < 0 || this.x > canvas.width) this.speedX = -this.speedX;
                if (this.y < 0 || this.y > canvas.height) this.speedY = -this.speedY;
            }
            draw() {
                ctx.fillStyle = this.color;
                ctx.beginPath();
                ctx.arc(this.x, this.y, this.size, 0, Math.PI * 2);
                ctx.fill();
            }
        }

        function initParticles() {
            const count = Math.min(Math.floor(window.innerWidth / 15), 80);
            for (let i = 0; i < count; i++) {
                particlesArray.push(new Particle());
            }
        }

        function animateParticles() {
            ctx.clearRect(0, 0, canvas.width, canvas.height);
            particlesArray.forEach(p => {
                p.update();
                p.draw();
            });
            requestAnimationFrame(animateParticles);
        }

        initParticles();
        animateParticles();
    }

    // ==========================================================================
    // 3. TYPING TEXT TAGLINE ANIMATION
    // ==========================================================================
    const taglineElem = document.getElementById('typing-tagline');
    if (taglineElem) {
        const phrases = [
            "Reconnect with your lost belongings.",
            "Centralized DBMS database verification.",
            "Fast, secure, and student-friendly."
        ];
        let phraseIdx = 0;
        let charIdx = 0;
        let isDeleting = false;
        let typingSpeed = 70;

        function typeLoop() {
            const currentPhrase = phrases[phraseIdx];
            if (isDeleting) {
                taglineElem.innerText = currentPhrase.substring(0, charIdx - 1);
                charIdx--;
                typingSpeed = 30;
            } else {
                taglineElem.innerText = currentPhrase.substring(0, charIdx + 1);
                charIdx++;
                typingSpeed = 70;
            }

            if (!isDeleting && charIdx === currentPhrase.length) {
                isDeleting = true;
                typingSpeed = 2500;
            } else if (isDeleting && charIdx === 0) {
                isDeleting = false;
                phraseIdx = (phraseIdx + 1) % phrases.length;
                typingSpeed = 500;
            }

            setTimeout(typeLoop, typingSpeed);
        }

        setTimeout(typeLoop, 1000);
    }

    // ==========================================================================
    // 4. DARK / LIGHT THEME TOGGLE HANDLER
    // ==========================================================================
    const themeBtn = document.getElementById('theme-btn');
    if (themeBtn) {
        themeBtn.addEventListener('click', () => {
            const currentTheme = document.body.getAttribute('data-theme') || 'dark';
            const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
            
            document.body.setAttribute('data-theme', newTheme);
            localStorage.setItem('appTheme', newTheme);

            if (newTheme === 'light') {
                themeBtn.innerHTML = '🌙';
                showFormAlert("Light Mode activated.");
            } else {
                themeBtn.innerHTML = '☀️';
                showFormAlert("Dark Mode activated.");
            }
        });

        const savedTheme = localStorage.getItem('appTheme');
        if (savedTheme === 'light') {
            document.body.setAttribute('data-theme', 'light');
            themeBtn.innerHTML = '🌙';
        }
    }

    // ==========================================================================
    // 5. ROLE TILES SELECTION CONTROLLER
    // ==========================================================================
    let selectedRole = 'student'; // Global auth role tracking variable

    window.switchLoginRole = function(role) {
        const studentTile = document.getElementById('tile-student');
        const adminTile = document.getElementById('tile-admin');
        const userLabel = document.getElementById('username-field-label');
        const userInput = document.getElementById('login-username');
        const passInput = document.getElementById('login-password');

        selectedRole = role;

        if (role === 'admin') {
            studentTile.classList.remove('active-student');
            adminTile.classList.add('active-admin');
            
            // Adjust fields
            if (userLabel) userLabel.innerText = "Admin Username";
            if (userInput) userInput.value = "admin";
            if (passInput) passInput.value = "admin123";
            
            showFormAlert("Session Mode: Selected Administrator credentials.");
        } else {
            adminTile.classList.remove('active-admin');
            studentTile.classList.add('active-student');
            
            if (userLabel) userLabel.innerText = "Student Seat ID / USN";
            if (userInput) userInput.value = "1RV24CS015";
            if (passInput) passInput.value = "password123";
            
            showFormAlert("Session Mode: Selected Student credentials.");
        }
    };

    // ==========================================================================
    // 6. FORM PANEL FLIPPING LOOPS
    // ==========================================================================
    const wrapper = document.getElementById('auth-panel-wrapper');
    window.switchLoginPanel = function(panelType) {
        if (panelType === 'register') {
            wrapper.classList.add('register-active');
            showFormAlert("Form loaded: Student Registration.");
        } else {
            wrapper.classList.remove('register-active');
            showFormAlert("Form loaded: Account Credentials Sign In.");
        }
    };

    // ==========================================================================
    // 7. PASSWORD VISIBILITY TOGGLE BUTTON
    // ==========================================================================
    window.togglePasswordReveal = function(btn, inputId) {
        const input = document.getElementById(inputId);
        if (input) {
            if (input.type === 'password') {
                input.type = 'text';
                btn.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width:16px; height:16px;"><path stroke-linecap="round" stroke-linejoin="round" d="M3.98 8.223A10.477 10.477 0 001.934 12C3.226 16.338 7.244 19.5 12 19.5c.993 0 1.953-.138 2.863-.395M6.228 6.228A10.45 10.45 0 0112 4.5c4.756 0 8.773 3.162 10.065 7.498a10.523 10.523 0 01-4.293 5.774M6.228 6.228L3 3m3.228 3.228l3.65 3.65m7.894 7.894L21 21m-3.228-3.228l-3.65-3.65m0 0a3 3 0 10-4.243-4.243m4.242 4.242L9.88 9.88" /></svg>`;
            } else {
                input.type = 'password';
                btn.innerHTML = `<svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="1.5" stroke="currentColor" style="width:16px; height:16px;"><path stroke-linecap="round" stroke-linejoin="round" d="M2.036 12.322a1.012 1.012 0 010-.644C3.799 6.545 7.11 3 12 3s8.2 3.545 9.964 8.678a1.012 1.012 0 010 .644C20.2 17.455 16.89 21 12 21s-8.2-3.545-9.964-8.678z" /><path stroke-linecap="round" stroke-linejoin="round" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /></svg>`;
            }
        }
    };

    // ==========================================================================
    // 8. VERIFICATION CHECK & SUCCESS POPUP WORKFLOW
    // ==========================================================================
    window.handleCyberLogin = function(btn) {
        const user = document.getElementById('login-username').value.trim();
        const pass = document.getElementById('login-password').value.trim();

        if (!user || !pass) {
            showFormAlert("Authentication Error: Input fields cannot be empty.");
            return;
        }

        const btnText = btn.querySelector('.btn-label-text');
        const loader = btn.querySelector('.btn-loader');
        
        btnText.style.display = 'none';
        loader.style.display = 'block';
        btn.disabled = true;

        setTimeout(() => {
            let role = selectedRole; // Loaded directly from the active tile state!
            let fullName = 'Aishu R. Kumar';
            let dept = 'Computer Science';

            if (role === 'admin') {
                fullName = 'System Administrator';
                dept = 'Office Operations';
            }

            // Write credential payloads to Local Storage
            localStorage.setItem('activeSessionRole', role);
            localStorage.setItem('activeSessionUser', user);
            localStorage.setItem('activeSessionName', fullName);
            localStorage.setItem('activeSessionDept', dept);

            loader.style.display = 'none';
            btnText.style.display = 'block';
            btnText.innerText = "✓ Authorized";
            btn.style.background = "linear-gradient(135deg, var(--neon-emerald) 0%, #059669 100%)";

            const modal = document.getElementById('success-modal');
            const progress = document.getElementById('success-progress-bar');
            
            modal.classList.add('show');

            let progressPct = 0;
            const progressInterval = setInterval(() => {
                progressPct += 5;
                progress.style.width = `${progressPct}%`;
                
                if (progressPct >= 100) {
                    clearInterval(progressInterval);
                    window.location.href = 'index.html';
                }
            }, 60);

        }, 1500);
    };

    // --- SUBMIT REGISTRATION ---
    window.handleCyberRegister = function(btn) {
        const name = document.getElementById('reg-fullname').value.trim();
        const usn = document.getElementById('reg-usn').value.trim();
        const dept = document.getElementById('reg-dept').value.trim();
        const pass = document.getElementById('reg-pass').value.trim();

        if (!name || !usn || !dept || !pass) {
            showFormAlert("Register Alert: Complete all forms!");
            return;
        }

        const btnText = btn.querySelector('.btn-label-text');
        const loader = btn.querySelector('.btn-loader');

        btnText.style.display = 'none';
        loader.style.display = 'block';
        btn.disabled = true;

        setTimeout(() => {
            loader.style.display = 'none';
            btnText.style.display = 'block';
            btn.disabled = false;
            
            showFormAlert("Account created successfully! Switching to Login.");
            document.getElementById('reg-fullname').value = '';
            document.getElementById('reg-usn').value = '';
            document.getElementById('reg-dept').value = '';
            document.getElementById('reg-pass').value = '';
            
            window.switchLoginRole('student');
            window.switchLoginPanel('login');
            document.getElementById('login-username').value = usn;
        }, 1200);
    };

    const alertBox = document.getElementById('portal-alert');
    function showFormAlert(message) {
        if (!alertBox) return;
        alertBox.querySelector('.noti-message').innerText = message;
        alertBox.classList.add('show');
        setTimeout(() => {
            alertBox.classList.remove('show');
        }, 2500);
    }
});
