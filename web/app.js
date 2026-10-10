/**
 * Campus Election Commission - Online Voting System Web Portal
 * Core Client-Side Logic, Form Validation, & State Management
 */

(function () {
  "use strict";

  // --- 1. LOCAL STORAGE & DATA INITIALIZATION (ALL VOTES ZERO, 10 PARTICIPANTS) ---
  const STORAGE_KEY = "campus_voting_system_data_v3";

  const DEFAULT_DATA = {
    candidates: [
      {
        id: 1,
        name: "Rahul Sharma",
        party: "Student Democratic Front",
        symbol: "🦅",
        manifesto: "Advocating for 24/7 digital lab access, upgraded library facilities, and transparent semester project evaluations.",
        votes: 0
      },
      {
        id: 2,
        name: "Anita Verma",
        party: "Youth Innovation Alliance",
        symbol: "💡",
        manifesto: "Focused on national hackathon sponsorships, industry internships, and tech club infrastructure funding.",
        votes: 0
      },
      {
        id: 3,
        name: "Suresh Iyer",
        party: "Campus United Voice",
        symbol: "🌟",
        manifesto: "Committed to hostel amenities modernization, student welfare grievance committee, and sports festival expansion.",
        votes: 0
      },
      {
        id: 4,
        name: "Priya Nair",
        party: "Creative Arts & Media Forum",
        symbol: "🎨",
        manifesto: "Dedicated to annual cultural fest revitalization, campus digital media studio, and creative arts clubs funding.",
        votes: 0
      },
      {
        id: 5,
        name: "Rohan Deshmukh",
        party: "Engineering & Tech Syndicate",
        symbol: "⚙️",
        manifesto: "Pushing for advanced robotics labs, cloud computing credits for students, and open-source project grants.",
        votes: 0
      },
      {
        id: 6,
        name: "Sneha Patel",
        party: "Eco-Green Campus Movement",
        symbol: "🌱",
        manifesto: "Advancing campus sustainability, solar energy on hostel roofs, waste recycling, and paperless administrative workflows.",
        votes: 0
      },
      {
        id: 7,
        name: "Mohammed Zeeshan",
        party: "Sports & Health League",
        symbol: "⚽",
        manifesto: "Upgrading sports complex amenities, inter-college athletic tournaments, and medical insurance coverage for student athletes.",
        votes: 0
      },
      {
        id: 8,
        name: "Ananya Roy",
        party: "Women in STEM & Equality Council",
        symbol: "🚀",
        manifesto: "Promoting mentorship programs, diversity scholarships, female leadership seminars, and safe campus transport.",
        votes: 0
      },
      {
        id: 9,
        name: "Tanmay Bhatia",
        party: "Entrepreneurship & Incubation Guild",
        symbol: "📈",
        manifesto: "Connecting students with venture capital seed funding, startup incubator facilities, and business pitch competitions.",
        votes: 0
      },
      {
        id: 10,
        name: "Divya Menon",
        party: "Academic Excellence & Research Collective",
        symbol: "📚",
        manifesto: "Expanding peer tutoring networks, research publication fee waivers, and international conference travel stipends.",
        votes: 0
      }
    ],
    voters: [
      {
        id: "101001",
        fullName: "Vikram Malhotra",
        dob: "2001-08-14",
        password: "voter123",
        hasVoted: false,
        votedFor: null,
        registeredAt: "2026-10-09T08:00:00.000Z"
      },
      {
        id: "101002",
        fullName: "Pooja Deshmukh",
        dob: "2002-03-22",
        password: "voter123",
        hasVoted: false,
        votedFor: null,
        registeredAt: "2026-10-09T08:15:00.000Z"
      },
      {
        id: "101003",
        fullName: "Aakash Banerjee",
        dob: "2001-11-05",
        password: "voter123",
        hasVoted: false,
        votedFor: null,
        registeredAt: "2026-10-09T08:30:00.000Z"
      },
      {
        id: "101004",
        fullName: "Neha Sharma",
        dob: "2002-01-19",
        password: "voter123",
        hasVoted: false,
        votedFor: null,
        registeredAt: "2026-10-09T08:45:00.000Z"
      },
      {
        id: "101005",
        fullName: "Jyotirmoy",
        dob: "2001-07-10",
        password: "voter123",
        hasVoted: false,
        votedFor: null,
        registeredAt: "2026-10-09T09:00:00.000Z"
      }
    ],
    votes: [],
    admin: {
      username: "admin",
      password: "admin123"
    }
  };

  function loadState() {
    try {
      const saved = localStorage.getItem(STORAGE_KEY);
      if (saved) {
        const parsed = JSON.parse(saved);
        // Verify valid structure and candidate count
        if (parsed && Array.isArray(parsed.candidates) && parsed.candidates.length >= 10) {
          return parsed;
        }
      }
    } catch (e) {
      console.warn("Could not parse existing state, resetting to default", e);
    }
    // Clean slate with 10 candidates and all votes set to 0
    saveState(DEFAULT_DATA);
    return JSON.parse(JSON.stringify(DEFAULT_DATA));
  }

  function saveState(data) {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(data));
  }

  let appState = loadState();
  let currentUser = null; // { role: 'voter'|'admin', id, name, hasVoted }
  let selectedCandidateForVote = null;

  // --- 2. DOM ELEMENT REFERENCES ---
  const navButtons = document.querySelectorAll(".nav-btn");
  const viewPanels = document.querySelectorAll(".view-panel");
  const sessionStatusText = document.getElementById("sessionStatusText");
  const logoutBtn = document.getElementById("logoutBtn");
  const navAdminBtn = document.getElementById("navAdminBtn");
  const navLoginBtn = document.getElementById("navLoginBtn");
  const navRegisterBtn = document.getElementById("navRegisterBtn");
  const globalNotification = document.getElementById("globalNotification");

  // Registration Elements
  const voterRegistrationForm = document.getElementById("voterRegistrationForm");
  const regVoterId = document.getElementById("regVoterId");
  const regFullName = document.getElementById("regFullName");
  const regDob = document.getElementById("regDob");
  const regPassword = document.getElementById("regPassword");
  const regConfirmPassword = document.getElementById("regConfirmPassword");

  // Login Elements
  const portalLoginForm = document.getElementById("portalLoginForm");
  const loginRole = document.getElementById("loginRole");
  const loginUserId = document.getElementById("loginUserId");
  const loginUserIdLabel = document.getElementById("loginUserIdLabel");
  const loginUserIdHint = document.getElementById("loginUserIdHint");
  const loginPassword = document.getElementById("loginPassword");

  // Ballot Elements
  const candidateListContainer = document.getElementById("candidateListContainer");
  const ballotStatusBadge = document.getElementById("ballotStatusBadge");
  const alreadyVotedBanner = document.getElementById("alreadyVotedBanner");

  // Modal Elements
  const voteConfirmModal = document.getElementById("voteConfirmModal");
  const modalCandidateBadge = document.getElementById("modalCandidateBadge");
  const modalCandidateSymbol = document.getElementById("modalCandidateSymbol");
  const modalCandidateName = document.getElementById("modalCandidateName");
  const modalCandidateParty = document.getElementById("modalCandidateParty");
  const btnConfirmVoteAction = document.getElementById("btnConfirmVoteAction");

  // Admin Elements
  const addCandidateForm = document.getElementById("addCandidateForm");
  const adminVoterTableBody = document.getElementById("adminVoterTableBody");

  // --- 3. VIEW SWITCHER ---
  window.switchView = function (targetViewId) {
    viewPanels.forEach(panel => {
      if (panel.id === targetViewId) {
        panel.classList.add("active");
      } else {
        panel.classList.remove("active");
      }
    });

    navButtons.forEach(btn => {
      if (btn.dataset.target === targetViewId) {
        btn.classList.add("active");
      } else {
        btn.classList.remove("active");
      }
    });

    // Refresh dynamic views
    if (targetViewId === "ballotView") {
      renderBallot();
    } else if (targetViewId === "resultsView") {
      renderLiveResults();
    } else if (targetViewId === "homeView") {
      updateHomeStats();
    } else if (targetViewId === "adminView") {
      renderAdminView();
    }

    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  navButtons.forEach(btn => {
    btn.addEventListener("click", () => {
      const target = btn.dataset.target;
      if (target) {
        switchView(target);
      }
    });
  });

  // --- 4. STRICT 6-DIGIT NUMERIC FILTER FOR VOTER ID ---
  function applyStrictNumericFilter(inputElement) {
    inputElement.addEventListener("keydown", function (e) {
      const allowedKeys = [
        "Backspace", "Delete", "Tab", "ArrowLeft", "ArrowRight", 
        "ArrowUp", "ArrowDown", "Home", "End", "Enter"
      ];
      if (allowedKeys.includes(e.key) || e.ctrlKey || e.metaKey) {
        return;
      }

      if (!/^[0-9]$/.test(e.key)) {
        e.preventDefault();
        showNotification("Voter ID accepts strictly digits (0-9). Characters like '" + e.key + "' are not permitted.", "error");
      }
    });

    inputElement.addEventListener("input", function () {
      const cleaned = this.value.replace(/\D/g, "").slice(0, 6);
      if (this.value !== cleaned) {
        this.value = cleaned;
      }
    });
  }

  applyStrictNumericFilter(regVoterId);

  // --- 5. NOTIFICATIONS ---
  function showNotification(message, type = "info") {
    globalNotification.textContent = message;
    globalNotification.className = "alert-box " + (type === "success" ? "alert-success" : type === "error" ? "alert-error" : "alert-info");
    globalNotification.style.display = "flex";

    window.scrollTo({ top: 0, behavior: "smooth" });

    clearTimeout(globalNotification._timer);
    globalNotification._timer = setTimeout(() => {
      globalNotification.style.display = "none";
    }, 5000);
  }

  // --- 6. VOTER REGISTRATION LOGIC ---
  voterRegistrationForm.addEventListener("submit", function (e) {
    e.preventDefault();

    const fullName = regFullName.value.trim();
    const voterId = regVoterId.value.trim();
    const dob = regDob.value;
    const password = regPassword.value;
    const confirmPassword = regConfirmPassword.value;

    if (!fullName) {
      showNotification("Please enter your full legal name.", "error");
      regFullName.focus();
      return;
    }

    if (voterId.length !== 6 || !/^\d{6}$/.test(voterId)) {
      showNotification("Voter ID must be exactly 6 digits (numbers only, e.g. 101001).", "error");
      regVoterId.focus();
      return;
    }

    if (!dob) {
      showNotification("Please select your Date of Birth using the calendar picker.", "error");
      regDob.focus();
      return;
    }

    if (!password || password.length < 4) {
      showNotification("Password must be at least 4 characters long.", "error");
      regPassword.focus();
      return;
    }

    if (password !== confirmPassword) {
      showNotification("Passwords do not match. Please re-enter carefully.", "error");
      regConfirmPassword.focus();
      return;
    }

    const existing = appState.voters.find(v => v.id === voterId);
    if (existing) {
      showNotification("Voter ID " + voterId + " is already registered in the electoral roll. Please sign in.", "error");
      return;
    }

    const newVoter = {
      id: voterId,
      fullName: fullName,
      dob: dob,
      password: password,
      hasVoted: false,
      votedFor: null,
      registeredAt: new Date().toISOString()
    };

    appState.voters.push(newVoter);
    saveState(appState);

    voterRegistrationForm.reset();
    showNotification("Registration Successful! Your Voter ID " + voterId + " is registered. Please sign in to vote.", "success");
    
    loginUserId.value = voterId;
    switchView("loginView");
  });

  // --- 7. ROLE & LOGIN MANAGEMENT ---
  loginRole.addEventListener("change", function () {
    if (this.value === "admin") {
      loginUserIdLabel.innerHTML = 'Admin Username <span class="required">*</span>';
      loginUserId.placeholder = "Enter administrative username";
      loginUserIdHint.textContent = "Authorized election commission administration login.";
    } else {
      loginUserIdLabel.innerHTML = 'Voter ID (6 Digits) <span class="required">*</span>';
      loginUserId.placeholder = "Enter your 6-digit Voter ID";
      loginUserIdHint.textContent = "Enter the 6-digit numeric ID registered with the portal.";
    }
    loginUserId.value = "";
    loginPassword.value = "";
  });

  portalLoginForm.addEventListener("submit", function (e) {
    e.preventDefault();

    const role = loginRole.value;
    const userId = loginUserId.value.trim();
    const password = loginPassword.value;

    if (!userId || !password) {
      showNotification("Please enter both ID and password to sign in.", "error");
      return;
    }

    if (role === "admin") {
      if (userId === appState.admin.username && password === appState.admin.password) {
        currentUser = {
          role: "admin",
          id: "admin",
          name: "System Administrator",
          hasVoted: false
        };
        handleSuccessfulLogin();
        showNotification("Welcome, Administrator! You have access to manage candidates and ballots.", "success");
        switchView("adminView");
      } else {
        showNotification("Invalid administrator credentials. Access denied.", "error");
      }
    } else {
      const voter = appState.voters.find(v => v.id === userId && v.password === password);
      if (voter) {
        currentUser = {
          role: "voter",
          id: voter.id,
          name: voter.fullName,
          hasVoted: voter.hasVoted
        };
        handleSuccessfulLogin();
        showNotification("Welcome, " + voter.fullName + "! You are authenticated to access the ballot paper.", "success");
        switchView("ballotView");
      } else {
        showNotification("Invalid Voter ID or password. Please verify and try again.", "error");
      }
    }
  });

  function handleSuccessfulLogin() {
    portalLoginForm.reset();
    sessionStatusText.textContent = currentUser.name + " (" + (currentUser.role === "admin" ? "Admin" : "Voter") + ")";
    logoutBtn.style.display = "inline-block";
    navLoginBtn.style.display = "none";
    navRegisterBtn.style.display = "none";

    if (currentUser.role === "admin") {
      navAdminBtn.style.display = "inline-flex";
    } else {
      navAdminBtn.style.display = "none";
    }

    updateHomeStats();
  }

  logoutBtn.addEventListener("click", function () {
    currentUser = null;
    sessionStatusText.textContent = "Voting Live • Guest";
    logoutBtn.style.display = "none";
    navLoginBtn.style.display = "inline-flex";
    navRegisterBtn.style.display = "inline-flex";
    navAdminBtn.style.display = "none";

    showNotification("You have been signed out safely.", "info");
    switchView("homeView");
  });

  // --- 8. OFFICIAL BALLOT RENDERING & VOTING ---
  function renderBallot() {
    candidateListContainer.innerHTML = "";

    if (!currentUser) {
      ballotStatusBadge.textContent = "Not Signed In (Sign in to Vote)";
      ballotStatusBadge.className = "badge badge-warning";
      alreadyVotedBanner.style.display = "none";
    } else if (currentUser.role === "admin") {
      ballotStatusBadge.textContent = "Logged in as Admin (Viewing Ballot)";
      ballotStatusBadge.className = "badge badge-warning";
      alreadyVotedBanner.style.display = "none";
    } else if (currentUser.hasVoted) {
      ballotStatusBadge.textContent = "Ballot Already Submitted (Voted)";
      ballotStatusBadge.className = "badge badge-success";
      alreadyVotedBanner.style.display = "block";
    } else {
      ballotStatusBadge.textContent = "Eligible to Vote: " + currentUser.name;
      ballotStatusBadge.className = "badge badge-success";
      alreadyVotedBanner.style.display = "none";
    }

    appState.candidates.forEach(candidate => {
      const card = document.createElement("div");
      card.className = "candidate-card";

      const canVote = currentUser && currentUser.role === "voter" && !currentUser.hasVoted;
      const buttonLabel = (!currentUser) 
        ? "Sign In to Vote" 
        : (currentUser.role === "admin") 
          ? "Admin View Only" 
          : (currentUser.hasVoted) 
            ? "Already Voted" 
            : "Vote for " + candidate.name;

      card.innerHTML = `
        <div>
          <div class="candidate-header">
            <div class="candidate-symbol-box">${candidate.symbol || "🗳️"}</div>
            <div class="candidate-info">
              <h3>${escapeHtml(candidate.name)}</h3>
              <p class="candidate-party">${escapeHtml(candidate.party)}</p>
            </div>
          </div>
          <p class="candidate-manifesto">"${escapeHtml(candidate.manifesto)}"</p>
        </div>
        <div class="candidate-action">
          <button 
            class="btn-vote" 
            ${!canVote ? "disabled" : ""} 
            onclick="promptVoteConfirmation(${candidate.id})">
            ${buttonLabel}
          </button>
        </div>
      `;

      candidateListContainer.appendChild(card);
    });
  }

  window.promptVoteConfirmation = function (candidateId) {
    if (!currentUser) {
      showNotification("Please sign in with your 6-digit Voter ID before casting a vote.", "error");
      switchView("loginView");
      return;
    }

    if (currentUser.role === "admin") {
      showNotification("Administrators oversee the election and cannot cast ballots.", "error");
      return;
    }

    if (currentUser.hasVoted) {
      showNotification("You have already cast your vote! One-voter-one-vote is strictly enforced.", "error");
      return;
    }

    const candidate = appState.candidates.find(c => c.id === candidateId);
    if (!candidate) return;

    selectedCandidateForVote = candidate;
    modalCandidateName.textContent = candidate.name;
    modalCandidateParty.textContent = candidate.party;
    modalCandidateSymbol.textContent = candidate.symbol || "🗳️";

    voteConfirmModal.style.display = "flex";
  };

  window.closeConfirmModal = function () {
    voteConfirmModal.style.display = "none";
    selectedCandidateForVote = null;
  };

  btnConfirmVoteAction.addEventListener("click", function () {
    if (!selectedCandidateForVote || !currentUser || currentUser.hasVoted) {
      closeConfirmModal();
      return;
    }

    // 1. Increment candidate vote
    selectedCandidateForVote.votes = (selectedCandidateForVote.votes || 0) + 1;

    // 2. Mark voter as voted in state
    const voter = appState.voters.find(v => v.id === currentUser.id);
    if (voter) {
      voter.hasVoted = true;
      voter.votedFor = selectedCandidateForVote.id;
    }

    // 3. Create masked vote log entry (preserving ballot secrecy)
    const voteRecord = {
      voteId: "VOTE-" + (1000 + appState.votes.length + 1),
      candidateId: selectedCandidateForVote.id,
      maskedVoterId: currentUser.id.slice(0, 2) + "****",
      timestamp: new Date().toISOString()
    };
    appState.votes.push(voteRecord);

    // 4. Update session
    currentUser.hasVoted = true;

    // 5. Save to local storage
    saveState(appState);

    const candidateName = selectedCandidateForVote.name;
    closeConfirmModal();

    showNotification("Vote Successfully Cast! Your ballot for " + candidateName + " has been sealed and recorded.", "success");
    renderBallot();
    updateHomeStats();
  });

  // --- 9. LIVE RESULTS CALCULATION ---
  function renderLiveResults() {
    const totalVotes = appState.candidates.reduce((sum, c) => sum + (c.votes || 0), 0);
    const registeredCount = appState.voters.length;
    const turnoutPct = registeredCount > 0 ? ((totalVotes / registeredCount) * 100).toFixed(1) : "0.0";

    document.getElementById("summaryTotalVotes").textContent = totalVotes;
    document.getElementById("summaryRegisteredVoters").textContent = registeredCount;
    document.getElementById("summaryTurnoutRate").textContent = turnoutPct + "%";

    // Find leading candidate
    let leader = null;
    let maxVotes = -1;
    let isTie = false;

    appState.candidates.forEach(c => {
      const v = c.votes || 0;
      if (v > maxVotes) {
        maxVotes = v;
        leader = c;
        isTie = false;
      } else if (v === maxVotes && v > 0) {
        isTie = true;
      }
    });

    const summaryLeaderName = document.getElementById("summaryLeaderName");
    if (totalVotes === 0 || maxVotes === 0) {
      summaryLeaderName.textContent = "No Votes Yet";
    } else if (isTie) {
      summaryLeaderName.textContent = "Tie (" + leader.name + " & others)";
    } else {
      summaryLeaderName.textContent = leader ? leader.name + " (" + leader.symbol + ")" : "None";
    }

    // Populate Table
    const tbody = document.getElementById("resultsTableBody");
    tbody.innerHTML = "";

    const sorted = [...appState.candidates].sort((a, b) => (b.votes || 0) - (a.votes || 0));

    sorted.forEach((c, index) => {
      const cVotes = c.votes || 0;
      const pct = totalVotes > 0 ? ((cVotes / totalVotes) * 100).toFixed(1) : "0.0";
      const isCurrentLeader = totalVotes > 0 && cVotes === maxVotes && !isTie;

      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td>
          <strong>${c.symbol || "🗳️"} ${escapeHtml(c.name)}</strong>
        </td>
        <td>${escapeHtml(c.party)}</td>
        <td><strong>${cVotes}</strong></td>
        <td>
          <div style="display:flex; align-items:center; gap: 8px;">
            <span>${pct}%</span>
          </div>
          <div class="progress-track">
            <div class="progress-bar ${isCurrentLeader ? "leader-bar" : ""}" style="width: ${pct}%;"></div>
          </div>
        </td>
        <td>
          ${isCurrentLeader ? '<span class="badge badge-success">🏆 Leading</span>' : '<span class="badge" style="background:#e2e8f0; color:#334155;">#' + (index + 1) + '</span>'}
        </td>
      `;
      tbody.appendChild(tr);
    });
  }

  window.renderLiveResults = renderLiveResults;

  // --- 10. ADMIN CONSOLE MANAGEMENT ---
  function renderAdminView() {
    renderAdminVoters();
  }

  function renderAdminVoters() {
    adminVoterTableBody.innerHTML = "";

    if (appState.voters.length === 0) {
      adminVoterTableBody.innerHTML = `<tr><td colspan="5" style="text-align:center; color:#94a3b8;">No registered voters found.</td></tr>`;
      return;
    }

    appState.voters.forEach(voter => {
      const tr = document.createElement("tr");
      tr.innerHTML = `
        <td><code>${escapeHtml(voter.id)}</code></td>
        <td><strong>${escapeHtml(voter.fullName)}</strong></td>
        <td>${escapeHtml(voter.dob)}</td>
        <td>
          ${voter.hasVoted 
            ? '<span class="badge badge-success">Ballot Cast</span>' 
            : '<span class="badge badge-warning">Awaiting Vote</span>'}
        </td>
        <td>
          <button class="btn-secondary" style="padding: 4px 10px; font-size: 0.8rem;" onclick="adminDeleteVoter('${voter.id}')">Delete</button>
        </td>
      `;
      adminVoterTableBody.appendChild(tr);
    });
  }

  addCandidateForm.addEventListener("submit", function (e) {
    e.preventDefault();

    const name = document.getElementById("newCandidateName").value.trim();
    const party = document.getElementById("newCandidateParty").value.trim();
    const symbol = document.getElementById("newCandidateSymbol").value.trim();

    if (!name || !party || !symbol) {
      showNotification("Please provide all candidate details.", "error");
      return;
    }

    const nextId = appState.candidates.length > 0 
      ? Math.max(...appState.candidates.map(c => c.id)) + 1 
      : 1;

    const newCandidate = {
      id: nextId,
      name: name,
      party: party,
      symbol: symbol,
      manifesto: "Pledged to represent student interests faithfully in the 2026 Student Academic Council.",
      votes: 0
    };

    appState.candidates.push(newCandidate);
    saveState(appState);

    addCandidateForm.reset();
    showNotification("Candidate " + name + " added to official ballot paper successfully!", "success");
    renderBallot();
    renderLiveResults();
    updateHomeStats();
  });

  window.adminDeleteVoter = function (voterId) {
    if (!confirm("Are you sure you want to remove Voter ID " + voterId + " from the electoral roll?")) {
      return;
    }

    appState.voters = appState.voters.filter(v => v.id !== voterId);
    saveState(appState);
    renderAdminVoters();
    updateHomeStats();
    showNotification("Voter record " + voterId + " deleted.", "info");
  };

  window.resetElectionVotes = function () {
    if (!confirm("Are you sure you want to reset all ballot tallies to 0? All registered voters will be marked as not having voted.")) {
      return;
    }

    appState.candidates.forEach(c => c.votes = 0);
    appState.voters.forEach(v => {
      v.hasVoted = false;
      v.votedFor = null;
    });
    appState.votes = [];

    if (currentUser && currentUser.role === "voter") {
      currentUser.hasVoted = false;
    }

    saveState(appState);
    showNotification("Election reset complete. All tallies set to zero.", "info");
    renderBallot();
    renderLiveResults();
    renderAdminVoters();
    updateHomeStats();
  };

  window.exportDataAsJSON = function () {
    const dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(appState, null, 2));
    const dlAnchorElem = document.createElement("a");
    dlAnchorElem.setAttribute("href", dataStr);
    dlAnchorElem.setAttribute("download", "campus_voting_system_export.json");
    dlAnchorElem.click();
    showNotification("Election database exported as campus_voting_system_export.json", "success");
  };

  // --- 11. HOME STATS SUMMARY ---
  function updateHomeStats() {
    const totalVotes = appState.candidates.reduce((sum, c) => sum + (c.votes || 0), 0);
    const registeredCount = appState.voters.length;
    const turnoutPct = registeredCount > 0 ? ((totalVotes / registeredCount) * 100).toFixed(1) : "0.0";

    const statCandidateCount = document.getElementById("statCandidateCount");
    const statVoterCount = document.getElementById("statVoterCount");
    const statBallotCount = document.getElementById("statBallotCount");
    const statTurnout = document.getElementById("statTurnout");

    if (statCandidateCount) statCandidateCount.textContent = appState.candidates.length;
    if (statVoterCount) statVoterCount.textContent = registeredCount;
    if (statBallotCount) statBallotCount.textContent = totalVotes;
    if (statTurnout) statTurnout.textContent = turnoutPct + "%";
  }

  function escapeHtml(text) {
    if (!text) return "";
    return text.toString()
      .replace(/&/g, "&amp;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/"/g, "&quot;")
      .replace(/'/g, "&#039;");
  }

  // --- 12. INITIAL BOOTSTRAP ---
  updateHomeStats();
  renderBallot();
  renderLiveResults();

})();
