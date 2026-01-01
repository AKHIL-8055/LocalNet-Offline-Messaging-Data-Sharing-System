# HelpHub - Offline Emergency Chat System

**HelpHub** is a lifesaving offline real-time chat system designed for emergency and disaster situations where internet connectivity is unavailable. It works entirely over local Wi-Fi/hotspot networks, making it perfect for refugee camps, flood areas, remote villages, and disaster zones.

## 🎯 Key Features

- **100% Offline Operation** - Works without internet, only requires local Wi-Fi/hotspot
- **Real-time Chat** - WebSocket-based instant messaging
- **File Sharing** - Upload and share photos, PDFs, and documents
- **Direct Messages** - Private messaging between users (`/to username message`)
- **SOS Emergency** - High-priority broadcast for emergencies
- **Message History** - Full conversation history loaded on connection
- **Auto-Reconnect** - Automatic reconnection with retry logic
- **Mobile-Friendly** - Responsive UI optimized for phones and tablets
- **Simple Authentication** - Username-based login (no passwords needed)

## 🏗️ Architecture

- **Backend**: Spring Boot (Java) with WebSocket, SQLite database
- **Frontend**: React (Vite) - served as static files from Spring Boot
- **Database**: SQLite (embedded, stored locally on server laptop)
- **Deployment**: Single JAR file - double-click to start server

## 📋 Prerequisites

- **Java 17+** (for running the server)
- **Node.js 18+** (for building frontend - only needed during development)
- **Maven** (for building the JAR)

## 🚀 Quick Start

### 1. Build the Project

#### Backend (Spring Boot)
```bash
mvn clean package
```
This creates `target/helphub-3.0.0.jar`

#### Frontend (React)
```bash
cd frontend
npm install
npm run build
```
This builds the frontend into `src/main/resources/static/`

### 2. Run the Server

**Option A: Run JAR directly**
```bash
java -jar target/helphub-3.0.0.jar
```

**Option B: Run from IDE**
Run `HelpHubApplication.java` main method

The server starts on **http://localhost:8080**

### 3. Access the Application

1. **On the server laptop**: Open browser to `http://localhost:8080`
2. **On connected devices**: Open browser to `http://[SERVER-IP]:8080`
   - Find server IP: `ipconfig` (Windows) or `ifconfig` (Linux/Mac)
   - Example: `http://192.168.1.100:8080`

### 4. Setup Local Network

1. **Create Wi-Fi Hotspot** on the server laptop
2. **Connect phones/devices** to the hotspot
3. **Access HelpHub** via the server IP address

## 📱 Usage Guide

### Registration & Login
1. **Register**: Enter username (unique), full name, and optional phone
2. **Login**: Enter existing username (no password needed)
3. Token is stored in browser localStorage

### Dashboard
- View server name and online user count
- Click "Connect to Server & Start Chat" to enter chat

### Chat Interface
- **Send Messages**: Type and click Send (or press Enter)
- **Direct Messages**: Type `/to username your message`
- **Upload Files**: Click 📎 button, select file (images, PDFs, docs)
- **SOS Button**: Click 🚨 SOS for emergency broadcast
- **Reconnect**: Click Reconnect if connection drops
- **Auto-scroll**: Chat automatically scrolls to latest messages

### File Sharing
- Supported: Images (JPG, PNG, GIF), PDFs, Documents
- Images show preview, other files show download link
- Files stored in `uploads/` directory on server

## 🔧 Configuration

Edit `src/main/resources/application.properties`:

```properties
# Server port
server.port=8080

# Server name (displayed to users)
app.server.name=Camp Alpha

# Upload directory
app.upload.dir=uploads

# JWT secret (change in production!)
jwt.secret=your-secret-key-here
```

## 📁 Project Structure

```
helphub/
├── src/main/java/com/helphub/
│   ├── HelpHubApplication.java      # Main application
│   ├── controller/                  # REST controllers
│   │   ├── AuthController.java
│   │   ├── FileUploadController.java
│   │   └── ServerInfoController.java
│   ├── service/                     # Business logic
│   │   ├── AuthService.java
│   │   ├── JwtService.java
│   │   └── MessageRoutingService.java
│   ├── websocket/                   # WebSocket handler
│   │   └── WebSocketHandler.java
│   ├── db/                          # Database layer
│   │   └── Db.java
│   ├── model/                       # Data models
│   │   ├── User.java
│   │   └── Message.java
│   └── config/                      # Configuration
│       ├── WebSocketConfig.java
│       ├── CorsConfig.java
│       └── StaticResourceConfig.java
├── frontend/                        # React frontend
│   ├── src/
│   │   ├── components/
│   │   │   ├── LoginRegister.jsx
│   │   │   ├── Dashboard.jsx
│   │   │   └── Chat.jsx
│   │   ├── services/
│   │   │   └── websocket.js
│   │   └── App.jsx
│   └── package.json
└── pom.xml                          # Maven dependencies
```

## 🗄️ Database Schema

**Users Table**
- id, username (unique), full_name, phone, created_at, online

**Messages Table**
- id, sender, sender_full_name, content, file_url, file_name, file_type, timestamp, is_sos, recipient

Database file: `helphub.db` (created automatically)

## 🔒 Security Notes

- **JWT tokens** stored in localStorage (change secret in production)
- **No password** authentication (simplified for offline use)
- **CORS enabled** for all origins (offline use case)
- **File uploads** limited to 10MB (configurable)

## 🐛 Troubleshooting

**Server won't start**
- Check Java version: `java -version` (need 17+)
- Check port 8080 is available
- Check SQLite JDBC dependency in pom.xml

**Frontend not loading**
- Ensure frontend is built: `cd frontend && npm run build`
- Check `src/main/resources/static/` contains built files

**WebSocket connection fails**
- Check server IP address is correct
- Ensure firewall allows port 8080
- Check token is valid in localStorage

**Files not uploading**
- Check `uploads/` directory exists and is writable
- Check file size (max 10MB)

## 🌟 Development Mode

### Run Frontend Separately (Development)
```bash
cd frontend
npm install
npm run dev
```
Frontend runs on `http://localhost:3000` with proxy to backend

### Run Backend Separately
```bash
mvn spring-boot:run
```
Backend runs on `http://localhost:8080`

## 📝 API Endpoints

- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `GET /api/server/info` - Get server info
- `POST /api/upload` - Upload file
- `GET /api/files/{filename}` - Download file
- `WS /ws?token={jwt}` - WebSocket connection

## 🎨 UI Features

- **Clean, modern design** optimized for emergency use
- **Red SOS button** with pulsing animation
- **Connection status** indicator (green/red dot)
- **Message timestamps** and sender names
- **File previews** for images
- **Mobile-responsive** layout
- **Dark/light** theme support

## 📦 Deployment

1. Build complete JAR: `mvn clean package`
2. Copy JAR to server laptop
3. Create `uploads/` directory next to JAR
4. Double-click JAR or run: `java -jar helphub-3.0.0.jar`
5. Share server IP with users
6. Users connect via browser (no app installation needed)

## 🤝 Contributing

This is a critical emergency tool. Please ensure:
- Code is well-tested
- Error handling is robust
- UI is simple and intuitive
- Documentation is clear

## 📄 License

This project is designed for humanitarian and emergency use.

## ⚠️ Important Notes

- **Test thoroughly** before deploying in real emergency situations
- **Backup database** regularly (`helphub.db`)
- **Monitor disk space** (files stored locally)
- **Change JWT secret** in production
- **Keep server laptop charged** (critical for operation)

---

**Built for emergencies. Built to save lives. 🚨**



