const express = require("express");
const fetch = require("node-fetch");
const cors = require("cors");
const https = require("https");
const fs = require("fs");

const app = express();
const PORT = 3001;
const SERVER_URL = "https://api.miloverada.gov.ua:8443/actuator/health";

let isServerUp = true;

app.use(cors());

// Load PKCS12 keystore

const options = {
  cert: fs.readFileSync('/etc/letsencrypt/live/api.miloverada.gov.ua/cert.pem'),
  key: fs.readFileSync('/etc/letsencrypt/live/api.miloverada.gov.ua/privkey.pem'),
  ca: fs.readFileSync('/etc/letsencrypt/live/api.miloverada.gov.ua/chain.pem')
};

const checkServerHealth = async () => {
  try {
    const res = await fetch(SERVER_URL, {timeout: 5000});

    if (res.ok) {
      const data = await res.json();
      isServerUp = data.status === "UP";
      console.log(`Server status: ${data.status}`);
    } else {
      isServerUp = false;
      console.error('Server health check failed with status:', res.status);
    }
  } catch (error) {
    console.log("Server is down");
    isServerUp = false;
  }
};

setInterval(checkServerHealth, 5000);

app.get("/health-proxy", (req, res) => {
  res.json({serverUp: isServerUp});
});

// Create HTTPS server with PKCS12 keystore
https.createServer(options, app).listen(PORT, () => {
  console.log(`Health Proxy running on https://localhost:${PORT}`);
});


