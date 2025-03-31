'use strict';

/**
 * add event on element
 */
const addEventOnElem = function (elem, type, callback) {
    if (elem.length > 1) {
        for (let i = 0; i < elem.length; i++) {
            elem[i].addEventListener(type, callback);
        }
    } else {
        elem.addEventListener(type, callback);
    }
}

/**
 * navbar toggle
 */
function initializeNavbar() {
    console.log("Initializing navbar");
    const navTogglers = document.querySelectorAll("[data-nav-toggler]");
    const navbar = document.querySelector(".mobile-navbar[data-navbar]"); // Specify mobile navbar
    const overlay = document.querySelector("[data-overlay]");
    console.log("Nav Togglers:", navTogglers);
    console.log("Navbar:", navbar);
    console.log("Overlay:", overlay);

    if (!navbar || !overlay) {
        console.error("Navbar or overlay not found!");
        return;
    }

    const toggleNavbar = function () {
        console.log("Toggling navbar");
        navbar.classList.toggle("active");
        overlay.classList.toggle("active");
    };

    navTogglers.forEach((toggler) => {
        toggler.addEventListener("click", toggleNavbar);
    });

    const navbarLinks = document.querySelectorAll(".mobile-navbar [data-nav-link]"); // Scope to mobile navbar
    const closeNavbar = function () {
        navbar.classList.remove("active");
        overlay.classList.remove("active");
    };

    navbarLinks.forEach((link) => {
        link.addEventListener("click", closeNavbar);
    });
}

/**
 * Navigation logic (login status, cart badge, redirects)
 */
function initializeNavigation() {
    console.log("Initializing navigation logic");

    // Utility function to get a cookie by name
    function getCookie(name) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; ${name}=`);
        if (parts.length === 2) {
            const cookieValue = parts.pop().split(';').shift();
            return cookieValue;
        }
        return null;
    }

    // Check if the user is logged in
    function isLoggedIn() {
        return !!getCookie('username');
    }

    // Update navigation based on login status
    const username = getCookie('username');
    const profileLink = document.getElementById('profile-btn');
    const loginLink = document.getElementById('login-btn');
    const cartBtn = document.getElementById('cart-btn');
    const favBtn = document.getElementById('fav-btn');

    if (username) {
        // User is logged in
        if (profileLink) profileLink.style.display = 'inline-block';
        if (loginLink) loginLink.style.display = 'none';
        if (cartBtn) cartBtn.style.display = 'inline-block';
        if (favBtn) favBtn.style.display = 'inline-block';
    } else {
        // User is not logged in
        if (profileLink) profileLink.style.display = 'none';
        if (loginLink) loginLink.style.display = 'inline-block';
        if (cartBtn) cartBtn.style.display = 'none';
        if (favBtn) favBtn.style.display = 'none';
    }

    // Update cart badge count
    const cart = JSON.parse(localStorage.getItem('cart')) || [];
    const cartCountElement = document.getElementById('cart-count');
    let totalItems = 0;
    cart.forEach(item => {
        totalItems += item.quantity;
    });
    if (cartCountElement) {
        cartCountElement.textContent = totalItems;
    }

    // Add event listener for cart button
    if (cartBtn) {
        cartBtn.addEventListener('click', () => {
            if (!isLoggedIn()) {
                window.location.href = 'login.html?redirect=' + encodeURIComponent(window.location.href);
                return;
            }
            window.location.href = 'cart.html';
        });
    }

    // Listen for cart updates (for same-tab updates)
    window.addEventListener('storage', (event) => {
        if (event.key === 'cart') {
            const updatedCart = JSON.parse(localStorage.getItem('cart')) || [];
            let updatedTotalItems = 0;
            updatedCart.forEach(item => {
                updatedTotalItems += item.quantity;
            });
            if (cartCountElement) {
                cartCountElement.textContent = updatedTotalItems;
            }
        }
    });
}

// Run initialization after w3-include-html loads
w3.includeHTML(() => {
    console.log("w3-include-html loaded successfully");
    initializeNavbar(); // Initialize navbar toggle
    initializeNavigation(); // Initialize navigation logic (login status, cart badge, etc.)
});

/**
 * header sticky & back top btn active
 */
const header = document.querySelector("[data-header]");
const backTopBtn = document.querySelector("[data-back-top-btn]");

const headerActive = function () {
    if (window.scrollY > 150) {
        header.classList.add("active");
        backTopBtn.classList.add("active");
    } else {
        header.classList.remove("active");
        backTopBtn.classList.remove("active");
    }
}

addEventOnElem(window, "scroll", headerActive);

let lastScrolledPos = 0;

const headerSticky = function () {
    if (lastScrolledPos >= window.scrollY) {
        header.classList.remove("header-hide");
    } else {
        header.classList.add("header-hide");
    }

    lastScrolledPos = window.scrollY;
}

addEventOnElem(window, "scroll", headerSticky);

/**
 * scroll reveal effect
 */
const sections = document.querySelectorAll("[data-section]");

const scrollReveal = function () {
    for (let i = 0; i < sections.length; i++) {
        if (sections[i].getBoundingClientRect().top < window.innerHeight / 2) {
            sections[i].classList.add("active");
        }
    }
}

scrollReveal();

addEventOnElem(window, "scroll", scrollReveal);

/**
 * footer video player
 */
w3.includeHTML(() => {
    const videoPlayer = document.getElementById("videoPlayer");
    const videoSource = document.getElementById("videoSource");

    if (videoPlayer && videoSource) {
        const videoList = [
            "./assets/videos/video1.mp4",
            "./assets/videos/video2.mp4",
            "./assets/videos/video3.mp4",
            "./assets/videos/video4.mp4"
        ];
        let videoIndex = 0;

        videoPlayer.addEventListener("ended", () => {
            videoIndex = (videoIndex + 1) % videoList.length; // Increment and wrap
            videoPlayer.pause(); // Pause before changing the source
            videoSource.src = videoList[videoIndex]; // Update source
            videoPlayer.load(); // Reload video
            videoPlayer.play().catch(error => {
                console.error("Error playing video:", error);
            });
        });
    } else {
        console.error("Video elements not found!");
    }
});