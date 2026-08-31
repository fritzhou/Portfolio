const config = window.PORTFOLIO_CONFIG;
const configured = value => Boolean(value) && !value.startsWith("YOUR_");
const contactIcons = { email: "✉", github: "⌘", facebook: "f", phone: "☎" };

const profileImage = document.querySelector("#profile-image");
if (profileImage && configured(config.profileImage)) {
  profileImage.src = config.profileImage;
  profileImage.addEventListener("error", () => {
    profileImage.src = "assets/profile-placeholder.svg";
  }, { once: true });
}

const technologyMap = new Map(window.TECHNOLOGIES.map(technology => [technology.name, technology]));

document.querySelector("#tech-grid").innerHTML = window.TECHNOLOGIES.map(technology => `
  <article class="tech-card card reveal">
    <img src="${technology.icon}" alt="${technology.name} logo" loading="lazy">
    <div><strong>${technology.name}</strong><small>${technology.note}</small></div>
  </article>
`).join("");

const createProjectPreview = project => `
  <div class="project-preview ${project.previewClass}" role="img" aria-label="Stylized application preview for ${project.name}">
    <div class="preview-toolbar"><span></span><span></span><span></span><small>${project.previewLabel}</small></div>
    <div class="preview-content">
      <div class="preview-sidebar"><i></i><i></i><i></i><i></i></div>
      <div class="preview-main">
        <div class="preview-kicker">PROJECT / ${project.category}</div>
        <strong>${project.name}</strong>
        <div class="preview-lines"><i></i><i></i><i></i></div>
        <div class="preview-panels"><span></span><span></span><span></span></div>
      </div>
    </div>
  </div>
`;

const projects = document.querySelector("#project-grid");
projects.innerHTML = window.PROJECTS.map(project => {
  const technologyBadges = project.technologies.map(name => {
    const technology = technologyMap.get(name);
    return technology ? `<span><img src="${technology.icon}" alt="" loading="lazy">${name}</span>` : `<span>${name}</span>`;
  }).join("");

  let primaryAction;
  if (project.downloadUrl) {
    primaryAction = `<a class="button primary" href="${project.downloadUrl}" aria-label="Download the Android app for ${project.name}">Download Android App <span aria-hidden="true">↓</span></a>`;
  } else if (project.liveUrl) {
    primaryAction = `<a class="button primary" href="${project.liveUrl}" target="_blank" rel="noopener noreferrer" aria-label="Open live demo of ${project.name}">Live demo <span aria-hidden="true">↗</span></a>`;
  } else {
    primaryAction = `<span class="button secondary project-status" aria-label="${project.name} does not have a public demo or download yet">In development</span>`;
  }

  const sourceAction = project.repoUrl
    ? `<a class="button secondary" href="${project.repoUrl}" target="_blank" rel="noopener noreferrer" aria-label="Open ${project.name} source code on GitHub">GitHub / Source <span aria-hidden="true">↗</span></a>`
    : "";

  return `
    <article class="project card reveal">
      ${createProjectPreview(project)}
      <div class="project-body">
        <p class="overline">${project.category}</p>
        <h3>${project.name}</h3>
        <p>${project.description}</p>
        <div class="project-tech" aria-label="Technologies used">${technologyBadges}</div>
        <div class="button-row project-actions">
          ${primaryAction}
          ${sourceAction}
        </div>
      </div>
    </article>
  `;
}).join("");

const contacts = [
  { key: "email", title: "Email", value: config.email, description: "Best for project and opportunity details." },
  { key: "github", title: "GitHub", value: config.githubUsername, description: "View my repositories and learning progress.", url: config.githubUrl },
  { key: "facebook", title: "Facebook", value: config.facebookName, description: "Connect with me on Facebook.", url: config.facebookUrl },
  { key: "phone", title: "Phone", value: config.phone, description: "Copy my contact number.", copy: true }
];

const contactGrid = document.querySelector("#contact-grid");
contactGrid.innerHTML = contacts.map(item => {
  const ready = configured(item.value) && (!item.url || configured(item.url));
  let action;

  if (!ready) {
    action = `<span class="text-action disabled-config">Add in config.js</span>`;
  } else if (item.copy) {
    action = `<button class="text-action copy-button" type="button" data-copy="${item.value}">Copy number</button>`;
  } else if (item.key === "email") {
    action = `<button class="text-action copy-button" type="button" data-copy="${item.value}">Copy email</button>`;
  } else {
    action = `<a class="text-action" href="${item.url}" target="_blank" rel="noopener noreferrer">Open profile <span aria-hidden="true">↗</span></a>`;
  }

  return `
    <article class="contact-card card reveal">
      <span class="contact-icon ${item.key}" aria-hidden="true">${contactIcons[item.key]}</span>
      <div>
        <p class="overline">${item.title}</p>
        <h3>${item.value}</h3>
        <p>${item.description}</p>
        ${action}
      </div>
    </article>
  `;
}).join("");

document.querySelectorAll(".copy-button").forEach(button => {
  button.addEventListener("click", async () => {
    try {
      await navigator.clipboard.writeText(button.dataset.copy);
      const original = button.textContent;
      button.textContent = "Copied!";
      setTimeout(() => { button.textContent = original; }, 1800);
    } catch {
      button.textContent = "Copy unavailable";
    }
  });
});

const gmail = document.querySelector("#gmail-link");
if (configured(config.email)) {
  const subject = "Portfolio Inquiry - Fritz Vohn";
  const mobileDevice = /Android|iPhone|iPad|iPod|Mobile/i.test(navigator.userAgent) || window.matchMedia("(pointer: coarse)").matches;

  if (mobileDevice) {
    gmail.href = `mailto:${encodeURIComponent(config.email)}?subject=${encodeURIComponent(subject)}`;
    gmail.removeAttribute("target");
    gmail.removeAttribute("rel");
    gmail.setAttribute("aria-label", "Compose an email in your mail app");
  } else {
    gmail.href = `https://mail.google.com/mail/?view=cm&fs=1&to=${encodeURIComponent(config.email)}&su=${encodeURIComponent(subject)}`;
    gmail.target = "_blank";
    gmail.rel = "noopener noreferrer";
    gmail.setAttribute("aria-label", "Compose an email in Gmail Web");
  }

  gmail.classList.remove("disabled-config");
}

const footerGithub = document.querySelector("#footer-github");
const footerFacebook = document.querySelector("#footer-facebook");
if (configured(config.githubUrl)) {
  footerGithub.href = config.githubUrl;
  footerGithub.target = "_blank";
  footerGithub.rel = "noopener noreferrer";
}
if (configured(config.facebookUrl)) {
  footerFacebook.href = config.facebookUrl;
  footerFacebook.target = "_blank";
  footerFacebook.rel = "noopener noreferrer";
} else {
  footerFacebook.hidden = true;
}

document.querySelector("#year").textContent = new Date().getFullYear();

const menuButton = document.querySelector(".menu-button");
const navLinks = document.querySelector(".nav-links");
menuButton.addEventListener("click", () => {
  const isOpen = menuButton.getAttribute("aria-expanded") === "true";
  menuButton.setAttribute("aria-expanded", String(!isOpen));
  menuButton.setAttribute("aria-label", isOpen ? "Open navigation" : "Close navigation");
  navLinks.classList.toggle("open", !isOpen);
});
navLinks.addEventListener("click", event => {
  if (event.target.closest("a")) {
    menuButton.setAttribute("aria-expanded", "false");
    menuButton.setAttribute("aria-label", "Open navigation");
    navLinks.classList.remove("open");
  }
});

if ("IntersectionObserver" in window) {
  const observer = new IntersectionObserver(entries => {
    entries.forEach(entry => {
      if (entry.isIntersecting) {
        entry.target.classList.add("visible");
        observer.unobserve(entry.target);
      }
    });
  }, { threshold: 0.08 });
  document.querySelectorAll(".reveal").forEach(element => observer.observe(element));
} else {
  document.querySelectorAll(".reveal").forEach(element => element.classList.add("visible"));
}
