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

function includeHTML(callback) {
  let elements = document.querySelectorAll("[w3-include-html]");
  let count = elements.length;
  if (count === 0) {
    if (callback) callback();
    return;
  }

  elements.forEach((el) => {
    let file = el.getAttribute("w3-include-html");
    fetch(file)
      .then((response) => response.text())
      .then((data) => {
        el.innerHTML = data;
        el.removeAttribute("w3-include-html");
        count--;
        if (count === 0 && callback) callback();
      })
      .catch(() => console.error("Error loading file: " + file));
  });
}

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

window.onload = function () {
  includeHTML(() => {
    console.log("w3-include-html loaded successfully");
    initializeNavbar(); // Run navbar script only after components are loaded
  });
};

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
 * footer
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