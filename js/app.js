const config = window.PORTFOLIO_CONFIG;
const icon = (name) => ({ mail: "✉", github: "⌘", facebook: "f", phone: "☎" }[name]);
const configured = (value) => value && !value.startsWith("YOUR_");

document.querySelector("#tech-grid").innerHTML = window.TECHNOLOGIES.map(tech => `
  <article class="tech-card card reveal"><img src="${tech.icon}" alt="${tech.name} logo" loading="lazy"><div><strong>${tech.name}</strong><small>${tech.note}</small></div></article>`).join("");

const projects = document.querySelector("#project-grid");
if (!window.PROJECTS.length) {
  projects.innerHTML = `<article class="project-empty card reveal"><div class="preview-window" aria-hidden="true"><span></span><span></span><span></span><div>&lt; first_project /&gt;</div></div><div><p class="overline">Work in progress</p><h3>The first project is being prepared.</h3><p>I won't invent work to fill this space. A project card—with its real preview, technologies, live demo, and source link—will appear here when it is ready to share.</p></div></article>`;
} else {
  projects.innerHTML = window.PROJECTS.map(project => `<article class="project card reveal"><img src="${project.image}" alt="Preview of ${project.name}"><div class="project-body"><p class="overline">${project.category}</p><h3>${project.name}</h3><p>${project.description}</p><div class="project-tech">${project.technologies.map(name => { const t = window.TECHNOLOGIES.find(x => x.name === name); return `<span><img src="${t.icon}" alt="">${name}</span>`; }).join("")}</div><div class="button-row"><a class="button primary" href="${project.liveUrl}" target="_blank" rel="noopener noreferrer">Live demo ↗</a><a class="button secondary" href="${project.repoUrl}" target="_blank" rel="noopener noreferrer">GitHub</a></div></div></article>`).join("");
}

const contacts = [
  { key: "email", title: "Email", value: config.email, description: "Best for project and opportunity details." },
  { key: "github", title: "GitHub", value: config.githubUsername, description: "View my repositories and progress.", url: config.githubUrl },
  { key: "facebook", title: "Facebook", value: config.facebookName, description: "Connect with me on Facebook.", url: config.facebookUrl },
  { key: "phone", title: "Phone", value: config.phone, description: "Copy my contact number.", copy: true }
];
document.querySelector("#contact-grid").innerHTML = contacts.map(item => {
  const ready = configured(item.value) && (!item.url || configured(item.url));
  const action = item.copy ? `<button class="text-action copy-button" data-copy="${ready ? item.value : ""}" ${ready ? "" : "disabled"}>${ready ? "Copy number" : "Add in config.js"}</button>` : `<a class="text-action ${ready ? "" : "disabled-config"}" href="${ready ? (item.url || `mailto:${item.value}`) : "#contact"}" ${ready ? 'target="_blank" rel="noopener noreferrer"' : ""}>${ready ? (item.key === "email" ? "Copy / email" : "Open profile") : "Add in config.js"} →</a>`;
  return `<article class="contact-card card reveal"><span class="contact-icon ${item.key}">${icon(item.key)}</span><div><p class="overline">${item.title}</p><h3>${item.value}</h3><p>${item.description}</p>${action}</div></article>`;
}).join("");

document.querySelectorAll(".copy-button").forEach(button => button.addEventListener("click", async () => { await navigator.clipboard.writeText(button.dataset.copy); const old = button.textContent; button.textContent = "Copied!"; setTimeout(() => button.textContent = old, 1800); }));
const gmail = document.querySelector("#gmail-link");
if (configured(config.email)) { gmail.href = `https://mail.google.com/mail/?view=cm&fs=1&to=${encodeURIComponent(config.email)}&su=${encodeURIComponent("Portfolio Inquiry - Fritz Vohn")}`; gmail.classList.remove("disabled-config"); }
if (configured(config.githubUrl)) document.querySelector("#footer-github").href = config.githubUrl;
if (configured(config.facebookUrl)) document.querySelector("#footer-facebook").href = config.facebookUrl;
document.querySelector("#year").textContent = new Date().getFullYear();

const menuButton = document.querySelector(".menu-button"), navLinks = document.querySelector(".nav-links");
menuButton.addEventListener("click", () => { const open = menuButton.getAttribute("aria-expanded") === "true"; menuButton.setAttribute("aria-expanded", String(!open)); navLinks.classList.toggle("open", !open); });
navLinks.addEventListener("click", event => { if (event.target.closest("a")) { menuButton.setAttribute("aria-expanded", "false"); navLinks.classList.remove("open"); } });
const observer = new IntersectionObserver(entries => entries.forEach(entry => { if (entry.isIntersecting) entry.target.classList.add("visible"); }), { threshold: .08 });
document.querySelectorAll(".reveal").forEach(el => observer.observe(el));
