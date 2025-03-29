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
    console.log("Initializing navbar after w3-include-html loads");

    const navTogglers = document.querySelectorAll("[data-nav-toggler]");
    const navbar = document.querySelector("[data-navbar]");
    const navbarLinks = document.querySelectorAll("[data-nav-link]");
    const overlay = document.querySelector("[data-overlay]");

    if (!navbar || !overlay) {
        console.error("Navbar or overlay not found! Ensure components are loaded.");
        return;
    }

    const toggleNavbar = function () {
        navbar.classList.toggle("active");
        overlay.classList.toggle("active");
    };

    navTogglers.forEach((toggler) => {
        toggler.addEventListener("click", toggleNavbar);
    });

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
<<<<<<< Updated upstream
    initializeNavbar();

    // 🔁 DELAY the profileIcon listener until after DOM is re-rendered
    setTimeout(() => {
      const profileIcon = document.getElementById('profile-icon');
      if (profileIcon) {
        profileIcon.addEventListener('click', function (e) {
          e.preventDefault();
          fetch('/check-session')
            .then(res => {
              if (!res.ok) throw new Error('Session check failed: ' + res.status);
              return res.json();
            })
            .then(data => {
              // console.log("Session check result:", data);
              // const welcome = document.getElementById("welcome-msg");
              if (data.loggedIn) {
                window.location.href = 'profile.html';
              } else {
                window.location.href = 'login.html?error=Please login first';
              }
            })
            .catch(err => {
              console.error('Session check failed', err);
              window.location.href = 'login.html?error=Session error';
            });
        });
      } else {
        console.warn("⚠️ profile-icon not found after includeHTML");
      }

      fetch('/check-session')
      .then(res => res.json())
      .then(data => {
        const welcome = document.getElementById("welcome-msg");
        if (data.loggedIn && data.username && welcome) {
          welcome.textContent = `Welcome, ${data.username}`;
        }
      })
      .catch(err => console.error("Failed to update welcome message:", err));
  
  }, 0);

   // 0ms timeout to ensure DOM is ready

    // const profileIcon = document.getElementById('profile-icon');

//     if (profileIcon) {
//       profileIcon.addEventListener('click', function (e) {
//         e.preventDefault();
//         fetch('/check-session')
//           .then(res => res.json())
//           .then(data => {
//             if (data.loggedIn) {
//               window.location.href = 'profile.html';
//             } else {
//               window.location.href = 'login.html?error=Please login first';
//             }
//           })
//           .catch(err => {
//             console.error('Session check failed', err);
//             window.location.href = 'login.html?error=Session error';
//           });
//       });
//     } else {
//       console.warn("⚠️ profile-icon not found when script ran");
//     }
  });
};
=======
    initializeNavbar(); // Initialize navbar toggle
    initializeNavigation(); // Initialize navigation logic (login status, cart badge, etc.)
});
>>>>>>> Stashed changes


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