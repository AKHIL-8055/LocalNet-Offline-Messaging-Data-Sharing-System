import React, { useState, useEffect } from 'react'
import LoginRegister from './components/LoginRegister'
import Dashboard from './components/Dashboard'
import Chat from './components/Chat'
import './App.css'

const API_BASE = '/api'

function App() {
  const [currentView, setCurrentView] = useState('login') // login, dashboard, chat
  const [user, setUser] = useState(null)
  const [token, setToken] = useState(null)

  useEffect(() => {
    // Check for stored auth
    const storedToken = localStorage.getItem('helphub_token')
    const storedUsername = localStorage.getItem('helphub_username')
    
    if (storedToken && storedUsername) {
      setToken(storedToken)
      setUser({ username: storedUsername })
      setCurrentView('dashboard')
    }
  }, [])

  const handleLogin = (userData, authToken) => {
    setUser(userData)
    setToken(authToken)
    localStorage.setItem('helphub_token', authToken)
    localStorage.setItem('helphub_username', userData.username)
    setCurrentView('dashboard')
  }

  const handleLogout = () => {
    setUser(null)
    setToken(null)
    localStorage.removeItem('helphub_token')
    localStorage.removeItem('helphub_username')
    setCurrentView('login')
  }

  const handleStartChat = () => {
    setCurrentView('chat')
  }

  const handleBackToDashboard = () => {
    setCurrentView('dashboard')
  }

  return (
    <div className="app">
      {currentView === 'login' && (
        <LoginRegister onLogin={handleLogin} />
      )}
      {currentView === 'dashboard' && (
        <Dashboard 
          user={user} 
          token={token}
          onLogout={handleLogout}
          onStartChat={handleStartChat}
        />
      )}
      {currentView === 'chat' && (
        <Chat 
          user={user}
          token={token}
          onBack={handleBackToDashboard}
          onLogout={handleLogout}
        />
      )}
    </div>
  )
}

export default App



