// import React, { useState, useEffect } from 'react'
// import './Dashboard.css'

// const API_BASE = '/api'

// function Dashboard({ user, token, onLogout, onStartChat }) {
//   const [serverInfo, setServerInfo] = useState({ serverName: 'Loading...', onlineUsers: 0 })
//   const [loading, setLoading] = useState(true)

//   useEffect(() => {
//     fetchServerInfo()
//     const interval = setInterval(fetchServerInfo, 5000) // Update every 5 seconds
//     return () => clearInterval(interval)
//   }, [])

//   const fetchServerInfo = async () => {
//     try {
//       const response = await fetch(`${API_BASE}/server/info`)
//       const data = await response.json()
//       setServerInfo(data)
//     } catch (err) {
//       console.error('Failed to fetch server info:', err)
//     } finally {
//       setLoading(false)
//     }
//   }

//   return (
//     <div className="dashboard-container">
//       <div className="dashboard-card">
//         <div className="dashboard-header">
//           <h1>Welcome, {user?.fullName || user?.username}!</h1>
//           <button onClick={onLogout} className="btn btn-secondary btn-sm">
//             Logout
//           </button>
//         </div>

//         <div className="server-info">
//           <div className="info-item">
//             <span className="info-label">Server:</span>
//             <span className="info-value">{serverInfo.serverName}</span>
//           </div>
//           <div className="info-item">
//             <span className="info-label">Online Users:</span>
//             <span className="info-value">{serverInfo.onlineUsers}</span>
//           </div>
//         </div>

//         <div className="dashboard-actions">
//           <button
//             onClick={onStartChat}
//             className="btn btn-primary btn-large"
//           >
//             Connect to Server & Start Chat
//           </button>
//         </div>

//         <div className="dashboard-footer">
//           <p>Connected via local network</p>
//           <p>No internet required</p>
//         </div>
//       </div>
//     </div>
//   )
// }

// export default Dashboard


import React, { useState, useEffect } from 'react'
import './Dashboard.css'

const API_BASE = '/api'

function Dashboard({ user, token, onLogout, onStartChat }) {
  const [serverInfo, setServerInfo] = useState({ serverName: 'Loading...', onlineUsers: 0 })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    fetchServerInfo()
    const interval = setInterval(fetchServerInfo, 5000) // Update every 5 seconds
    return () => clearInterval(interval)
  }, [])

  const fetchServerInfo = async () => {
    try {
      const response = await fetch(`${API_BASE}/server/info`)
      const data = await response.json()
      setServerInfo(data)
    } catch (err) {
      console.error('Failed to fetch server info:', err)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-card">
        <div className="dashboard-header">
          <h1>Welcome, {user?.fullName || user?.username}!</h1>
          <button onClick={onLogout} className="dashboard-btn dashboard-btn-secondary dashboard-btn-sm">
            Logout
          </button>
        </div>

        <div className="server-info">
          <div className="info-item">
            <span className="info-label">Server:</span>
            <span >{serverInfo.serverName}</span>
          </div>
          <div className="info-item">
            <span className="info-label">Online Users:</span>
            <span >{serverInfo.onlineUsers}</span>
          </div>
        </div>

        <div className="dashboard-actions">
          <button
            onClick={onStartChat}
            className="dashboard-btn dashboard-btn-primary dashboard-btn-large"
          >
            Connect to Server
          </button>
        </div>

        <div className="dashboard-footer">
          <p> Connected via local network</p>
        </div>
      </div>
    </div>
  )
}

export default Dashboard




