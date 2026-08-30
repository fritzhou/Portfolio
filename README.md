# Fritz Vohn — Student Developer Portfolio

A modern, responsive, card-based personal portfolio for Fritz Vohn, a Grade 12 TVL-ICT student at Lourdes College. The site presents my learning journey, selected projects, technologies I am practicing, and ways to contact me without overstating my experience.

![Portfolio preview](assets/portfolio-preview.svg)

## Live Website

**GitHub Pages:** https://fritzhou.github.io/Portfolio/

**Repository:** https://github.com/fritzhou/Portfolio

## Features

- Modern card-based UI for desktop, tablet, and mobile
- Responsive mobile navigation and touch-friendly controls
- Honest student-developer positioning and open-for-opportunity section
- Technology cards using recognizable Devicon logos
- Data-driven project cards generated from `js/data.js`
- Designed browser-style project previews instead of unrelated stock or AI artwork
- Live Demo and GitHub/Source actions for verified projects
- Configurable contact cards
- Gmail compose shortcut with a pre-filled portfolio subject
- Copy-to-clipboard interaction for contact details
- Semantic HTML, visible focus states, reduced-motion support, and alt/ARIA labels
- SEO description, Open Graph metadata, favicon, and automatic footer year

## Selected Projects

The portfolio currently presents verified repositories for:

- **VoiceBox** — complaints and suggestions system
- **School FAQ Chatbot** — database-driven school FAQ capstone
- **Lourdes College SHS Site** — Senior High School information website
- **StockFlow Inventory System** — full-stack inventory/POS learning project still in development

StockFlow intentionally has no fake Live Demo link while it remains under active development.

## Technologies

The portfolio itself uses:

- HTML5
- CSS3
- Vanilla JavaScript
- Git / GitHub
- GitHub Pages

Project technology cards also document tools currently being practiced across my work, including Supabase, PostgreSQL, React, TypeScript, Python, and FastAPI.

## Local Setup

No build step is required.

```bash
git clone https://github.com/fritzhou/Portfolio.git
cd Portfolio
python3 -m http.server 8080
```

Then open `http://localhost:8080`.

## Project Structure

```text
├── assets/
│   ├── favicon.svg
│   └── portfolio-preview.svg
├── js/
│   ├── app.js           # Project/contact rendering and interactions
│   ├── config.js        # Contact and social configuration
│   └── data.js          # Technology and project data
├── index.html           # Semantic page structure and metadata
├── styles.css           # Main design system and responsive layout
├── project-cards.css    # Project preview/card presentation
└── README.md
```

## Adding Another Project

Add one object to `window.PROJECTS` in `js/data.js`. Keep project information together in that single object:

```js
{
  name: "Project Name",
  category: "Project Category",
  previewLabel: "Short preview label",
  previewClass: "project-theme-name",
  description: "What it does, what problem it addresses, and what I learned.",
  technologies: ["HTML5", "CSS3", "JavaScript"],
  repoUrl: "https://github.com/USERNAME/REPOSITORY",
  liveUrl: "https://real-deployment.example.com"
}
```

Use `liveUrl: null` when a project is not publicly deployed. The portfolio will display an **In development** state instead of creating a fake link.

If a new technology is needed, add it once to `window.TECHNOLOGIES` in the same file with its Devicon/SVG URL. Project cards will reuse it automatically.

## Contact Configuration

Edit `js/config.js` to update personal contact information. GitHub and email are already configured. The following values are intentionally still placeholders until real details are supplied:

- Phone number
- Facebook profile name
- Facebook profile URL

## Deployment

The repository is public and uses `main` as its default branch. GitHub Pages is connected to the repository, and pushes to `main` trigger the Pages build/deployment workflow automatically.
