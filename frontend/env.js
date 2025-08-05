// This script will replace environment variables in the built Angular application
// with the values from the container environment variables.
// It should be run before starting the Nginx server.

const fs = require('fs');
const path = require('path');

// Path to the main.js file in the built Angular application
const mainJsPath = path.join('/usr/share/nginx/html', 'main.*.js');

// Find the main.js file
const mainJsFile = fs.readdirSync('/usr/share/nginx/html')
  .find(file => /^main\.[0-9a-f]+\.js$/.test(file));

if (!mainJsFile) {
  console.error('Could not find main.js file');
  process.exit(1);
}

const mainJsFilePath = path.join('/usr/share/nginx/html', mainJsFile);

// Read the content of the main.js file
let content = fs.readFileSync(mainJsFilePath, 'utf8');

// Replace environment variables
const environmentVariables = {
  API_URL: process.env.API_URL || 'http://localhost:8050',
  AUTH_SERVER_URL: process.env.AUTH_SERVER_URL || 'http://localhost:8090'
};

// Replace each environment variable in the content
Object.entries(environmentVariables).forEach(([key, value]) => {
  // Create a regex that matches the environment variable in the Angular environment object
  const regex = new RegExp(`(["']?${key}["']?\\s*:\\s*["'])([^"']*)(["'])`, 'g');
  content = content.replace(regex, `$1${value}$3`);
});

// Write the modified content back to the file
fs.writeFileSync(mainJsFilePath, content);

console.log('Environment variables replaced successfully');
