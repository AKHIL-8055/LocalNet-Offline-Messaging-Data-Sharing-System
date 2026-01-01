/**
 * WebSocket service with auto-reconnect functionality
 */

class WebSocketService {
  constructor() {
    this.ws = null
    this.url = null
    this.token = null
    this.reconnectAttempts = 0
    this.maxReconnectAttempts = Infinity
    this.reconnectDelay = 1000
    this.listeners = new Map()
    this.isManualClose = false
  }

  connect(url, token) {
    this.url = url
    this.token = token
    this.isManualClose = false
    this.reconnectAttempts = 0
    this._connect()
  }

  _connect() {
    try {
      const wsUrl = `${this.url}?token=${this.token}`
      this.ws = new WebSocket(wsUrl)

      this.ws.onopen = () => {
        console.log('WebSocket connected')
        this.reconnectAttempts = 0
        this.emit('connected')
      }

      this.ws.onmessage = (event) => {
        try {
          const message = JSON.parse(event.data)
          this.emit('message', message)
        } catch (err) {
          console.error('Failed to parse message:', err)
        }
      }

      this.ws.onerror = (error) => {
        console.error('WebSocket error:', error)
        this.emit('error', error)
      }

      this.ws.onclose = () => {
        console.log('WebSocket closed')
        this.emit('disconnected')
        
        if (!this.isManualClose) {
          this._reconnect()
        }
      }
    } catch (err) {
      console.error('Failed to create WebSocket:', err)
      this.emit('error', err)
      if (!this.isManualClose) {
        setTimeout(() => this._reconnect(), this.reconnectDelay)
      }
    }
  }

  _reconnect() {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++
      const delay = Math.min(this.reconnectDelay * this.reconnectAttempts, 10000)
      console.log(`Reconnecting in ${delay}ms (attempt ${this.reconnectAttempts})...`)
      
      setTimeout(() => {
        if (!this.isManualClose) {
          this._connect()
        }
      }, delay)
    }
  }

  send(data) {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(data))
      return true
    }
    return false
  }

  sendMessage(content, fileUrl = null, fileName = null, fileType = null, recipient = null) {
    return this.send({
      type: 'message',
      content,
      fileUrl,
      fileName,
      fileType,
      recipient
    })
  }

  sendSOS(content = 'SOS - Emergency assistance needed!') {
    return this.send({
      type: 'sos',
      content
    })
  }

  on(event, callback) {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, [])
    }
    this.listeners.get(event).push(callback)
  }

  off(event, callback) {
    if (this.listeners.has(event)) {
      const callbacks = this.listeners.get(event)
      const index = callbacks.indexOf(callback)
      if (index > -1) {
        callbacks.splice(index, 1)
      }
    }
  }

  emit(event, data) {
    if (this.listeners.has(event)) {
      this.listeners.get(event).forEach(callback => {
        try {
          callback(data)
        } catch (err) {
          console.error('Error in event listener:', err)
        }
      })
    }
  }

  disconnect() {
    this.isManualClose = true
    if (this.ws) {
      this.ws.close()
      this.ws = null
    }
    this.listeners.clear()
  }

  isConnected() {
    return this.ws && this.ws.readyState === WebSocket.OPEN
  }

  getReadyState() {
    if (!this.ws) return WebSocket.CLOSED
    return this.ws.readyState
  }
}

export default new WebSocketService()



