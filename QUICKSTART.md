# HelpHub Quick Start Guide

## 🚀 5-Minute Setup

### Step 1: Build Backend
```bash
mvn clean package
```

### Step 2: Build Frontend
```bash
cd frontend
npm install
npm run build
cd ..
```

### Step 3: Run Server
**Windows:**
```bash
run.bat
```

**Linux/Mac:**
```bash
chmod +x run.sh
./run.sh
```

**Or directly:**
```bash
java -jar target/helphub-3.0.0.jar
```

### Step 4: Setup Network
1. **On Server Laptop:**
   - Create Wi-Fi Hotspot (Windows: Settings > Network > Mobile Hotspot)
   - Note your IP address: `ipconfig` → IPv4 Address (e.g., 192.168.137.1)

2. **On Phones/Devices:**
   - Connect to the hotspot
   - Open browser: `http://[SERVER-IP]:8080`
   - Example: `http://192.168.137.1:8080`

### Step 5: Use HelpHub
1. **Register** a new user (username, full name, optional phone)
2. **Login** with username
3. Click **"Connect to Server & Start Chat"**
4. Start chatting!

## 📱 First User Flow

1. **Register** → Enter username, full name
2. **Dashboard** → See server name, online users
3. **Chat** → Send messages, files, SOS alerts
4. **Direct Messages** → Type `/to username your message`

## 🔧 Troubleshooting

**Can't connect?**
- Check server is running: `http://localhost:8080`
- Check firewall allows port 8080
- Verify IP address is correct

**WebSocket fails?**
- Check browser console for errors
- Verify token in localStorage
- Try reconnecting

**Files not uploading?**
- Check `uploads/` folder exists
- Check disk space
- Max file size: 10MB

## 📝 Configuration

Edit `src/main/resources/application.properties`:
- `server.port=8080` - Change port if needed
- `app.server.name=Camp Alpha` - Change server display name
- `app.upload.dir=uploads` - Change upload directory

## 🎯 Key Features

- ✅ **Offline** - No internet needed
- ✅ **Real-time** - Instant messaging
- ✅ **Files** - Share photos, PDFs
- ✅ **SOS** - Emergency alerts
- ✅ **DM** - Direct messages
- ✅ **History** - Full chat history
- ✅ **Mobile** - Works on phones

## 💡 Tips

- **Server IP changes?** Restart hotspot to get new IP
- **Many users?** Monitor server resources
- **Backup data?** Copy `helphub.db` file
- **Change server name?** Edit `application.properties`

---

**Ready to save lives! 🚨**



