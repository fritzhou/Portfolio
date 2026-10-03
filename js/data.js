window.TECHNOLOGIES = [
  { name: "HTML5", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/html5/html5-original.svg", note: "Structure" },
  { name: "CSS3", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/css3/css3-original.svg", note: "Styling" },
  { name: "JavaScript", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/javascript/javascript-original.svg", note: "Interaction" },
  { name: "Python", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/python/python-original.svg", note: "Programming" },
  { name: "Kotlin", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/kotlin/kotlin-original.svg", note: "Android development" },
  { name: "XML", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/xml/xml-original.svg", note: "Android layouts" },
  { name: "Firebase", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/firebase/firebase-original.svg", note: "Mobile services" },
  { name: "Git", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/git/git-original.svg", note: "Version control" },
  { name: "GitHub", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/github/github-original.svg", note: "Collaboration" },
  { name: "Supabase", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/supabase/supabase-original.svg", note: "Backend & database" },
  { name: "PostgreSQL", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/postgresql/postgresql-original.svg", note: "Relational database" },
  { name: "React", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/react/react-original.svg", note: "Frontend library" },
  { name: "TypeScript", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/typescript/typescript-original.svg", note: "Typed JavaScript" },
  { name: "FastAPI", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/fastapi/fastapi-original.svg", note: "Python API framework" },
  { name: "Godot", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/godot/godot-original.svg", note: "Game engine" },
  { name: "GDScript", icon: "https://cdn.jsdelivr.net/gh/devicons/devicon@latest/icons/godot/godot-original.svg", note: "Godot scripting" }
];

window.PROJECTS = [
  {
    name: "NeuMusic",
    category: "Offline Android Music Player",
    previewLabel: "Music player & premium themes",
    previewClass: "neumusic",
    description: "An offline-first Android music player focused on a polished listening experience. NeuMusic supports local music playback, premium visual themes, playback and audio tools, listening insights, optional account and sync features, and backup/restore while keeping the core player useful offline.",
    technologies: ["Kotlin", "XML", "Supabase"],
    repoUrl: "https://github.com/fritzhou/neumusic",
    liveUrl: "https://neumusic.vercel.app"
  },
  {
    name: "Lunari",
    category: "Android Budget & Savings Tracker",
    previewLabel: "Personal finance dashboard",
    previewClass: "lunari",
    description: "A personal finance application for tracking money across e-wallets such as GCash, bank accounts, and credit cards. Lunari brings together balances, income, expenses, transfers, budgets, savings tracking, and financial goals in a clean mobile dashboard.",
    technologies: ["Kotlin", "XML"],
    repoUrl: null,
    liveUrl: null
  },
  {
    name: "Buhay Probinsya",
    category: "3D Filipino Life & Farming Simulator",
    previewLabel: "v0.8 · Active development",
    previewClass: "buhay-probinsya",
    description: "An ongoing 3D Filipino provincial-life simulator built in Godot. The project combines farming, NPC interactions, enterable places, vehicles, daily activities, local environments, and long-term life-simulation systems inspired by everyday life in the Philippine province. The current build is v0.8 and is still being expanded and polished.",
    technologies: ["Godot", "GDScript"],
    repoUrl: null,
    liveUrl: null
  },
  {
    name: "QR Attendance System",
    category: "QR-Code Based Attendance Website",
    previewLabel: "Attendance monitoring platform",
    previewClass: "attendance",
    description: "A QR-code based attendance website designed to make attendance recording faster and more organized. It includes student registration, QR generation and scanning, attendance monitoring, and separate workflows for school staff while keeping the frontend built with plain web technologies.",
    technologies: ["HTML5", "CSS3", "JavaScript"],
    repoUrl: "https://github.com/fritzhou/attendancesystem",
    liveUrl: null
  },
  {
    name: "VoiceBox Web",
    category: "Web-Based Complaint & Suggestion System",
    previewLabel: "School web platform",
    previewClass: "voicebox",
    description: "The web-based version of VoiceBox for school complaints and suggestions. It provides browser-based submission and tracking flows while helping me practice responsive frontend development, JavaScript application logic, and Supabase-backed data features.",
    technologies: ["HTML5", "CSS3", "JavaScript", "Supabase"],
    repoUrl: "https://github.com/fritzhou/voicebox",
    liveUrl: "https://voicebox-pi.vercel.app"
  },
  {
    name: "VoiceBox Android App",
    category: "Android Complaint & Suggestion System",
    previewLabel: "Native school Android app",
    previewClass: "voicebox-app",
    description: "The Android application version of VoiceBox, built for students to use the complaint and suggestion system from their phones. It uses Kotlin and XML for the native Android interface together with Firebase and Supabase services for application and backend features.",
    technologies: ["Kotlin", "XML", "Firebase", "Supabase"],
    repoUrl: null,
    downloadUrl: "https://github.com/fritzhou/voicebox/releases/download/v18.00/app-release.apk",
    liveUrl: null
  },
  {
    name: "School FAQ Chatbot",
    category: "Capstone Project",
    previewLabel: "Database-driven school FAQ",
    previewClass: "chatbot",
    description: "A keyword-based school FAQ chatbot with English, Filipino, and Cebuano support plus an admin dashboard for managing questions and answers. It helped me practice database-first thinking, Supabase integration, and making school information easier to maintain.",
    technologies: ["HTML5", "CSS3", "JavaScript", "Supabase"],
    repoUrl: "https://github.com/fritzhou/chatbot",
    liveUrl: "https://fritzhou.github.io/chatbot/"
  },
  {
    name: "Lourdes College SHS Site",
    category: "School Website",
    previewLabel: "Senior High School information hub",
    previewClass: "school",
    description: "A multi-page Senior High School website concept with faculty information and Supabase-backed management features. I used it to practice responsive layouts, information architecture, and organizing a larger plain HTML/CSS/JavaScript project.",
    technologies: ["HTML5", "CSS3", "JavaScript", "Supabase"],
    repoUrl: "https://github.com/fritzhou/lcshs",
    liveUrl: "https://fritzhou.github.io/lcshs/"
  },
  {
    name: "StockFlow Inventory System",
    category: "Full-Stack Learning Project",
    previewLabel: "Inventory & point-of-sale system",
    previewClass: "stockflow",
    description: "A larger inventory and POS project I am building while learning full-stack application architecture. The project combines a React/TypeScript frontend with a FastAPI backend and relational data storage, and is still actively being developed rather than presented as finished production software.",
    technologies: ["React", "TypeScript", "FastAPI", "Python", "PostgreSQL", "Supabase"],
    repoUrl: "https://github.com/fritzhou/InventorySystem",
    liveUrl: null
  }
];
