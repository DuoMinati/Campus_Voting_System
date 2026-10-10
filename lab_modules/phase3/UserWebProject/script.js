let savedUser = null;
const registerSection = document.getElementById("registerSection");
const loginSection = document.getElementById("loginSection");
const message = document.getElementById("message");

// Show the login form
document.getElementById("showLogin").addEventListener("click", function () {
  registerSection.hidden = true;
  loginSection.hidden = false;
  message.textContent = "";
});

// Show the registration form
document.getElementById("showRegister").addEventListener("click", function () {
  loginSection.hidden = true;
  registerSection.hidden = false;
  message.textContent = "";
});

// Handle registration
document.getElementById("registerForm").addEventListener("submit", function (event) {
  event.preventDefault();
  const username = document.getElementById("regUsername").value.trim();
  const password = document.getElementById("regPassword").value;
  document.getElementById("regUsernameError").textContent = "";
  document.getElementById("regPasswordError").textContent = "";
  message.textContent = "";
  let valid = true;

  if (username === "") {
    document.getElementById("regUsernameError").textContent = "Username cannot be empty.";
    valid = false;
  } else if (username.length < 5) {
    document.getElementById("regUsernameError").textContent = "Username must contain at least 5 characters.";
    valid = false;
  }

  if (password === "") {
    document.getElementById("regPasswordError").textContent = "Password cannot be empty.";
    valid = false;
  } else if (password.length < 8) {
    document.getElementById("regPasswordError").textContent = "Password must contain at least 8 characters.";
    valid = false;
  } else if (!/[0-9]/.test(password)) {
    document.getElementById("regPasswordError").textContent = "Password must contain at least one digit.";
    valid = false;
  }

  if (valid) {
    savedUser = {
      username: username,
      password: password
    };
    message.textContent = "Registration successful. You can now log in.";
    registerSection.hidden = true;
    loginSection.hidden = false;
  }
});

// Handle login
document.getElementById("loginForm").addEventListener("submit", function (event) {
  event.preventDefault();
  const username = document.getElementById("loginUsername").value.trim();
  const password = document.getElementById("loginPassword").value;
  message.textContent = "";

  if (username === "" || password === "") {
    message.textContent = "Please enter both username and password.";
    return;
  }

  if (
    savedUser !== null &&
    username === savedUser.username &&
    password === savedUser.password
  ) {
    message.textContent = "Login successful. Welcome, " + username + "!";
  } else {
    message.textContent = "Invalid username or password.";
  }
});
