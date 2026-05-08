// import React, { useState } from 'react'
// import './LoginRegister.css'

// const API_BASE = '/api'

// function LoginRegister({ onLogin }) {
//   const [isLogin, setIsLogin] = useState(true)
//   const [username, setUsername] = useState('')
//   const [fullName, setFullName] = useState('')
//   const [phone, setPhone] = useState('')
//   const [error, setError] = useState('')
//   const [loading, setLoading] = useState(false)

//   const handleSubmit = async (e) => {
//     e.preventDefault()
//     setError('')
//     setLoading(true)

//     try {
//       const endpoint = isLogin ? '/api/auth/login' : '/api/auth/register'
//       const body = isLogin 
//         ? { username }
//         : { username, fullName, phone: phone || null }

//       const response = await fetch(endpoint, {
//         method: 'POST',
//         headers: {
//           'Content-Type': 'application/json',
//         },
//         body: JSON.stringify(body),
//       })

//       const data = await response.json()

//       if (data.success) {
//         onLogin(
//           { username: data.username, fullName: data.fullName },
//           data.token
//         )
//       } else {
//         setError(data.message || 'Operation failed')
//       }
//     } catch (err) {
//       setError('Network error. Please check server connection.')
//     } finally {
//       setLoading(false)
//     }
//   }

//   return (
//     <>
//     <div className="login-register-container">


//       <div className="login-register-card">
//         <div className="logo">
//           <h1>HelpHub</h1>
//           <p>Emergency Offline Chat System</p>
//         </div>

//         <div className="tabs">
//           <button
//             className={`tab ${isLogin ? 'active' : ''}`}
//             onClick={() => setIsLogin(true)}
//           >
//             Login
//           </button>
//           <button
//             className={`tab ${!isLogin ? 'active' : ''}`}
//             onClick={() => setIsLogin(false)}
//           >
//             Register
//           </button>
//         </div>

//         <form onSubmit={handleSubmit} className="auth-form">
//           <div className="form-group">
//             <label>Username *</label>
//             <input
//               type="text"
//               value={username}
//               onChange={(e) => setUsername(e.target.value)}
//               required
//               placeholder="Enter username"
//               className="input"
//             />
//           </div>

//           {!isLogin && (
//             <>
//               <div className="form-group">
//                 <label>Full Name *</label>
//                 <input
//                   type="text"
//                   value={fullName}
//                   onChange={(e) => setFullName(e.target.value)}
//                   required
//                   placeholder="Enter your full name"
//                   className="input"
//                 />
//               </div>

//               <div className="form-group">
//                 <label>Phone (Optional)</label>
//                 <input
//                   type="tel"
//                   value={phone}
//                   onChange={(e) => setPhone(e.target.value)}
//                   placeholder="Enter phone number"
//                   className="input"
//                 />
//               </div>
//             </>
//           )}

//           {error && <div className="error-message">{error}</div>}

//           <button
//             type="submit"
//             className="btn btn-primary btn-block"
//             disabled={loading}
//           >
//             {loading ? 'Processing...' : isLogin ? 'Login' : 'Register'}
//           </button>
//         </form>

//         <div className="info-box">
//           <p> This system works offline via local Wi-Fi/hotspot</p>
//           <p>No internet connection required</p>
//         </div>
//       </div>
//     </div>
//     </>
//   )
// }

// export default LoginRegister



import React, { useState } from 'react'
import './LoginRegister.css'

function LoginRegister({ onLogin }) {
  const [isLogin, setIsLogin] = useState(true)
  const [username, setUsername] = useState('')
  const [password, setPassword] = useState('')
  const [fullName, setFullName] = useState('')
  const [phone, setPhone] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setLoading(true)

    try {
      const endpoint = isLogin ? '/api/auth/login' : '/api/auth/register'
      const body = isLogin 
        ? { username, password }
        : { username, password, fullName, phone: phone || null }

      const response = await fetch(endpoint, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(body),
      })

      const data = await response.json()

      if (data.success) {
        onLogin(
          { username: data.username, fullName: data.fullName },
          data.token
        )
      } else {
        setError(data.message || 'Operation failed')
      }
    } catch (err) {
      setError('Network error. Please check server connection.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-register-container">
      <div className="login-register-card">
        <div>
          <div className="logo">
            <h1>HelpHub</h1>
            <p>Reliable Communication When Internet Fails</p>
          </div>

          <div className="tabs">
            <button
              className={`tab ${isLogin ? 'active' : ''}`}
              onClick={() => setIsLogin(true)}
            >
              Login
            </button>
            <button
              className={`tab ${!isLogin ? 'active' : ''}`}
              onClick={() => setIsLogin(false)}
            >
              Register
            </button>
          </div>

          <form onSubmit={handleSubmit} className="auth-form">
            <div className="form-group">
              <label>Username *</label>
              <input
                type="text"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
                placeholder="Enter username"
                className="input"
              />
            </div>

            <div className="form-group">
              <label>Password *</label>
              <input
                type="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
                placeholder="Enter password"
                className="input"
              />
            </div>

            {!isLogin && (
              <>
                <div className="form-group">
                  <label>Full Name *</label>
                  <input
                    type="text"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    required
                    placeholder="Enter your full name"
                    className="input"
                  />
                </div>

                <div className="form-group">
                  <label>Phone (Optional)</label>
                  <input
                    type="tel"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                    placeholder="Enter phone number"
                    className="input"
                  />
                </div>
              </>
            )}

            {error && <div className="error-message">{error}</div>}

            <button
              type="submit"
              className="btn btn-primary btn-block dashboard-btn dashboard-btn-primary dashboard-btn-large"
              disabled={loading}
            >
              {loading ? 'Processing...' : isLogin ? 'Login' : 'Register'}
            </button>
          </form>

          <div className="info-box">
            <p>Connected via local network</p>
          </div>
        </div>
      </div>

      <div className="image-side"></div>
    </div>
  )
}

export default LoginRegister