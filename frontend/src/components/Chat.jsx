// import React, { useState, useEffect, useRef } from 'react'
// import websocket from '../services/websocket'
// import './Chat.css'

// const API_BASE = '/api'

// function Chat({ user, token, onBack, onLogout }) {
//   const [messages, setMessages] = useState([])
//   const [input, setInput] = useState('')
//   const [connectionStatus, setConnectionStatus] = useState('disconnected')
//   const [serverName, setServerName] = useState('Loading...')
//   const [fileInput, setFileInput] = useState(null)
//   const [uploading, setUploading] = useState(false)
//   const [dmRecipient, setDmRecipient] = useState('')
//   const messagesEndRef = useRef(null)
//   const fileInputRef = useRef(null)

//   useEffect(() => {
//     // Get server info
//     fetchServerInfo()

//     // Setup WebSocket
//     const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
//     const wsUrl = `${protocol}//${window.location.host}/ws`
//     websocket.connect(wsUrl, token)

//     // WebSocket event handlers
//     const handleConnected = () => {
//       setConnectionStatus('connected')
//     }

//     const handleDisconnected = () => {
//       setConnectionStatus('disconnected')
//     }

//     const handleMessage = (message) => {
//       setMessages(prev => [...prev, message])
//     }

//     const handleError = (error) => {
//       console.error('WebSocket error:', error)
//       setConnectionStatus('error')
//     }

//     websocket.on('connected', handleConnected)
//     websocket.on('disconnected', handleDisconnected)
//     websocket.on('message', handleMessage)
//     websocket.on('error', handleError)

//     // Cleanup
//     return () => {
//       websocket.off('connected', handleConnected)
//       websocket.off('disconnected', handleDisconnected)
//       websocket.off('message', handleMessage)
//       websocket.off('error', handleError)
//       websocket.disconnect()
//     }
//   }, [token])

//   useEffect(() => {
//     scrollToBottom()
//   }, [messages])

//   const fetchServerInfo = async () => {
//     try {
//       const response = await fetch(`${API_BASE}/server/info`)
//       const data = await response.json()
//       setServerName(data.serverName)
//     } catch (err) {
//       console.error('Failed to fetch server info:', err)
//     }
//   }

//   const scrollToBottom = () => {
//     messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
//   }

//   const handleSend = async () => {
//     if (!input.trim() && !fileInput) return

//     let fileUrl = null
//     let fileName = null
//     let fileType = null

//     // Upload file if present
//     if (fileInput) {
//       setUploading(true)
//       try {
//         const formData = new FormData()
//         formData.append('file', fileInput)

//         const response = await fetch(`${API_BASE}/upload`, {
//           method: 'POST',
//           body: formData,
//         })

//         const data = await response.json()
//         if (data.success) {
//           fileUrl = data.fileUrl
//           fileName = data.fileName
//           fileType = data.fileType
//         } else {
//           alert('Failed to upload file: ' + data.message)
//           setUploading(false)
//           return
//         }
//       } catch (err) {
//         alert('Failed to upload file: ' + err.message)
//         setUploading(false)
//         return
//       }
//       setUploading(false)
//     }

//     // Parse DM recipient if message starts with /to
//     let recipient = null
//     let messageContent = input.trim()
    
//     if (messageContent.startsWith('/to ')) {
//       const parts = messageContent.substring(4).trim().split(/\s+/, 2)
//       if (parts.length >= 1 && parts[0]) {
//         recipient = parts[0]
//         messageContent = parts.length > 1 ? parts[1] : ''
//       }
//     }

//     // Send message via WebSocket
//     const success = websocket.sendMessage(
//       messageContent || (fileInput ? `Shared ${fileName}` : ''),
//       fileUrl,
//       fileName,
//       fileType,
//       recipient
//     )

//     if (success) {
//       setInput('')
//       setFileInput(null)
//       if (fileInputRef.current) {
//         fileInputRef.current.value = ''
//       }
//     } else {
//       alert('Failed to send message. Please reconnect.')
//     }
//   }

//   const handleSOS = () => {
//     if (confirm('Send SOS emergency message to all users?')) {
//       websocket.sendSOS('SOS - Emergency assistance needed!')
//     }
//   }

//   const handleReconnect = () => {
//     const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
//     const wsUrl = `${protocol}//${window.location.host}/ws`
//     websocket.disconnect()
//     setTimeout(() => {
//       websocket.connect(wsUrl, token)
//     }, 500)
//   }

//   const handleFileSelect = (e) => {
//     const file = e.target.files[0]
//     if (file) {
//       setFileInput(file)
//     }
//   }

//   const formatTime = (timestamp) => {
//     if (!timestamp) return ''
//     try {
//       const date = new Date(timestamp)
//       return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
//     } catch {
//       return timestamp
//     }
//   }

//   const isImage = (fileType) => {
//     return fileType === 'image'
//   }

//   const getStatusColor = () => {
//     switch (connectionStatus) {
//       case 'connected': return '#27ae60'
//       case 'disconnected': return '#e74c3c'
//       case 'error': return '#e74c3c'
//       default: return '#95a5a6'
//     }
//   }

//   return (
//     <div className="chat-container">
//       <div className="chat-header">
//         <div className="chat-header-left">
//           <button onClick={onBack} className="btn-back">Back</button>
//           <div className="server-info-header">
//             <span className="server-name">{serverName}</span>
//             <div className="connection-status">
//               <span 
//                 className="status-dot" 
//                 style={{ backgroundColor: getStatusColor() }}
//               ></span>
//               <span className="status-text">
//                 {connectionStatus === 'connected' ? 'Connected' : 
//                  connectionStatus === 'disconnected' ? 'Disconnected' : 'Error'}
//               </span>
//             </div>
//           </div>
//         </div>
//         <div className="chat-header-right">
//           <button onClick={handleReconnect} className="btn btn-secondary btn-sm">
//             Reconnect
//           </button>
//           <button onClick={onLogout} className="btn btn-secondary btn-sm">
//             Logout
//           </button>
//         </div>
//       </div>

//       <div className="chat-messages" id="chat-messages">
//         {messages.map((msg, index) => {
//           const isOwn = msg.sender === user?.username
//           const isSystem = msg.sender === 'SYSTEM'
//           const isDM = msg.recipient && msg.recipient === user?.username
//           const isSentDM = msg.recipient && msg.sender === user?.username

//           return (
//             <div
//               key={index}
//               className={`message ${isOwn ? 'own' : ''} ${isSystem ? 'system' : ''} ${msg.isSOS ? 'sos' : ''} ${isDM || isSentDM ? 'dm' : ''}`}
//             >
//               {!isSystem && (
//                 <div className="message-header">
//                   <span className="message-sender">
//                     {msg.senderFullName || msg.sender}
//                     {isDM && <span className="dm-badge">DM</span>}
//                     {msg.isSOS && <span className="sos-badge">SOS</span>}
//                   </span>
//                   <span className="message-time">{formatTime(msg.timestamp)}</span>
//                 </div>
//               )}
//               {msg.content && (
//                 <div className="message-content">{msg.content}</div>
//               )}
//               {msg.fileUrl && (
//                 <div className="message-file">
//                   {isImage(msg.fileType) ? (
//                     <img 
//                       src={msg.fileUrl} 
//                       alt={msg.fileName || 'Image'} 
//                       className="message-image"
//                       onClick={() => window.open(msg.fileUrl, '_blank')}
//                     />
//                   ) : (
//                     <a 
//                       href={msg.fileUrl} 
//                       target="_blank" 
//                       rel="noopener noreferrer"
//                       className="message-file-link"
//                     >
//                       File {msg.fileName || 'File'}
//                     </a>
//                   )}
//                 </div>
//               )}
//             </div>
//           )
//         })}
//         <div ref={messagesEndRef} />
//       </div>

//       <div className="chat-input-area">
//         <div className="input-row">
//           <input
//             type="text"
//             value={input}
//             onChange={(e) => setInput(e.target.value)}
//             onKeyPress={(e) => e.key === 'Enter' && !e.shiftKey && handleSend()}
//             placeholder={dmRecipient ? `DM to ${dmRecipient}` : "Type message... (use /to username message for DM)"}
//             className="chat-input"
//             disabled={uploading || connectionStatus !== 'connected'}
//           />
//           <input
//             type="file"
//             ref={fileInputRef}
//             onChange={handleFileSelect}
//             className="file-input"
//             id="file-input"
//             accept="image/*,application/pdf,.doc,.docx"
//           />
//           <label htmlFor="file-input" className="btn btn-secondary btn-file">
//             File
//           </label>
//           {fileInput && (
//             <span className="file-name">{fileInput.name}</span>
//           )}
//         </div>
//         <div className="action-row">
//           <button
//             onClick={handleSOS}
//             className="btn btn-sos"
//             disabled={connectionStatus !== 'connected'}
//           >
//              SOS
//           </button>
//           <button
//             onClick={handleSend}
//             className="btn btn-primary"
//             disabled={uploading || (!input.trim() && !fileInput) || connectionStatus !== 'connected'}
//           >
//             {uploading ? 'Uploading...' : 'Send'}
//           </button>
//         </div>
//       </div>
//     </div>
//   )
// }

// export default Chat



import React, { useState, useEffect, useRef } from 'react'
import websocket from '../services/websocket'
import './Chat.css'

const API_BASE = '/api'

function Chat({ user, token, onBack, onLogout }) {
  const [messages, setMessages] = useState([])
  const [input, setInput] = useState('')
  const [connectionStatus, setConnectionStatus] = useState('disconnected')
  const [serverName, setServerName] = useState('Loading...')
  const [fileInput, setFileInput] = useState(null)
  const [uploading, setUploading] = useState(false)
  const [dmRecipient, setDmRecipient] = useState('')
  const messagesEndRef = useRef(null)
  const fileInputRef = useRef(null)

  useEffect(() => {
    // Get server info
    fetchServerInfo()

    // Setup WebSocket
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    const wsUrl = `${protocol}//${window.location.host}/ws`
    websocket.connect(wsUrl, token)

    // WebSocket event handlers
    const handleConnected = () => {
      setConnectionStatus('connected')
    }

    const handleDisconnected = () => {
      setConnectionStatus('disconnected')
    }

    const handleMessage = (message) => {
      setMessages(prev => [...prev, message])
    }

    const handleError = (error) => {
      console.error('WebSocket error:', error)
      setConnectionStatus('error')
    }

    websocket.on('connected', handleConnected)
    websocket.on('disconnected', handleDisconnected)
    websocket.on('message', handleMessage)
    websocket.on('error', handleError)

    // Cleanup
    return () => {
      websocket.off('connected', handleConnected)
      websocket.off('disconnected', handleDisconnected)
      websocket.off('message', handleMessage)
      websocket.off('error', handleError)
      websocket.disconnect()
    }
  }, [token])

  useEffect(() => {
    scrollToBottom()
  }, [messages])

  const fetchServerInfo = async () => {
    try {
      const response = await fetch(`${API_BASE}/server/info`)
      const data = await response.json()
      setServerName(data.serverName)
    } catch (err) {
      console.error('Failed to fetch server info:', err)
    }
  }

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' })
  }

  const handleSend = async () => {
    if (!input.trim() && !fileInput) return

    let fileUrl = null
    let fileName = null
    let fileType = null

    // Upload file if present
    if (fileInput) {
      setUploading(true)
      try {
        const formData = new FormData()
        formData.append('file', fileInput)

        const response = await fetch(`${API_BASE}/upload`, {
          method: 'POST',
          body: formData,
        })

        const data = await response.json()
        if (data.success) {
          fileUrl = data.fileUrl
          fileName = data.fileName
          fileType = data.fileType
        } else {
          alert('Failed to upload file: ' + data.message)
          setUploading(false)
          return
        }
      } catch (err) {
        alert('Failed to upload file: ' + err.message)
        setUploading(false)
        return
      }
      setUploading(false)
    }

    // Parse DM recipient if message starts with /to
    let recipient = null
    let messageContent = input.trim()
    
    if (messageContent.startsWith('/to ')) {
      const parts = messageContent.substring(4).trim().split(/\s+/, 2)
      if (parts.length >= 1 && parts[0]) {
        recipient = parts[0]
        messageContent = parts.length > 1 ? parts[1] : ''
      }
    }

    // Send message via WebSocket
    const success = websocket.sendMessage(
      messageContent || (fileInput ? `Shared ${fileName}` : ''),
      fileUrl,
      fileName,
      fileType,
      recipient
    )

    if (success) {
      setInput('')
      setFileInput(null)
      if (fileInputRef.current) {
        fileInputRef.current.value = ''
      }
    } else {
      alert('Failed to send message. Please reconnect.')
    }
  }

  const handleSOS = () => {
    if (confirm('Send SOS emergency message to all users?')) {
      websocket.sendSOS('SOS - Emergency assistance needed!')
    }
  }

  const handleReconnect = () => {
    const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:'
    const wsUrl = `${protocol}//${window.location.host}/ws`
    websocket.disconnect()
    setTimeout(() => {
      websocket.connect(wsUrl, token)
    }, 500)
  }

  const handleFileSelect = (e) => {
    const file = e.target.files[0]
    if (file) {
      setFileInput(file)
    }
  }

  const formatTime = (timestamp) => {
    if (!timestamp) return ''
    try {
      const date = new Date(timestamp)
      return date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    } catch {
      return timestamp
    }
  }

  const isImage = (fileType) => {
    return fileType === 'image'
  }

  const getStatusColor = () => {
    switch (connectionStatus) {
      case 'connected': return '#27ae60'
      case 'disconnected': return '#e74c3c'
      case 'error': return '#e74c3c'
      default: return '#95a5a6'
    }
  }

  return (
    <div className="chat-container">
      <div className="chat-header">
        <div className="chat-header-left">
          <button onClick={onBack} className="btn-back">Back</button>
          <div className="server-info-header">
            <span className="server-name">{serverName}</span>
            <div className="connection-status">
              <span 
                className="status-dot" 
                style={{ backgroundColor: getStatusColor() }}
              ></span>
              <span className="status-text">
                {connectionStatus === 'connected' ? 'Connected' : 
                 connectionStatus === 'disconnected' ? 'Disconnected' : 'Error'}
              </span>
            </div>
          </div>
        </div>
        <div className="chat-header-right">
          <button onClick={handleReconnect} className="btn btn-secondary btn-sm col">
            Reconnect
          </button>
          <button onClick={onLogout} className="btn btn-secondary btn-sm col">
            Logout
          </button>
        </div>
      </div>

      <div className="chat-messages" id="chat-messages">
        {messages.map((msg, index) => {
          const isOwn = msg.sender === user?.username
          const isSystem = msg.sender === 'SYSTEM'
          const isDM = msg.recipient && msg.recipient === user?.username
          const isSentDM = msg.recipient && msg.sender === user?.username

          return (
            <div
              key={index}
              className={`message ${isOwn ? 'own' : ''} ${isSystem ? 'system' : ''} ${msg.isSOS ? 'sos' : ''} ${isDM || isSentDM ? 'dm' : ''}`}
            >
              {!isSystem && (
                <div className="message-header">
                  <span className="message-sender">
                    {msg.senderFullName || msg.sender}
                    {isDM && <span className="dm-badge">DM</span>}
                    {msg.isSOS && <span className="sos-badge">SOS</span>}
                  </span>
                  <span className="message-time">{formatTime(msg.timestamp)}</span>
                </div>
              )}
              {msg.content && (
                <div className="message-content">{msg.content}</div>
              )}
              {msg.fileUrl && (
                <div className="message-file">
                  {isImage(msg.fileType) ? (
                    <img 
                      src={msg.fileUrl} 
                      alt={msg.fileName || 'Image'} 
                      className="message-image"
                      onClick={() => window.open(msg.fileUrl, '_blank')}
                    />
                  ) : (
                    <a 
                      href={msg.fileUrl} 
                      target="_blank" 
                      rel="noopener noreferrer"
                      className="message-file-link"
                    >
                      {msg.fileName || 'File'}
                    </a>
                  )}
                </div>
              )}
            </div>
          )
        })}
        <div ref={messagesEndRef} />
      </div>

      <div className="chat-input-area">
        <div className="input-row">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyPress={(e) => e.key === 'Enter' && !e.shiftKey && handleSend()}
            placeholder={dmRecipient ? `DM to ${dmRecipient}` : "Message"}
            className="chat-input"
            disabled={uploading || connectionStatus !== 'connected'}
          />
          <input
            type="file"
            ref={fileInputRef}
            onChange={handleFileSelect}
            className="file-input"
            id="file-input"
            accept="image/*,application/pdf,.doc,.docx"
          />
          <label htmlFor="file-input" className="btn btn-secondary btn-file">
            <p>File</p>
          </label>
          {fileInput && (
            <span className="file-name">{fileInput.name}</span>
          )}
        </div>
        <div className="action-row">
          {/* <button
            onClick={handleSOS}
            className="btn btn-sos"
            disabled={connectionStatus !== 'connected'}
          >
            SOS
          </button> */}
          <button
            onClick={handleSend}
            className="btn btn-primary col"
            disabled={uploading || (!input.trim() && !fileInput) || connectionStatus !== 'connected'}
          >
            {uploading ? 'Uploading...' : 'Send'}
          </button>
        </div>
      </div>
    </div>
  )
}

export default Chat



