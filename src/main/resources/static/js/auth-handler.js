/**
 * Authentication Handler
 * Handles JWT token management and automatic token refresh
 */

class AuthHandler {
    constructor() {
        this.tokenRefreshInProgress = false;
        this.setupTokenRefresh();
    }

    // Check if access token will expire soon (within 5 minutes)
    isTokenExpiringSoon() {
        const token = this.getTokenFromCookie('jwt_token');
        if (!token) return true;
        
        try {
            // Parse JWT payload
            const payload = JSON.parse(atob(token.split('.')[1]));
            const expiryTime = payload.exp * 1000; // Convert to milliseconds
            const currentTime = Date.now();
            const timeUntilExpiry = expiryTime - currentTime;
            
            // Return true if token expires in less than 5 minutes
            return timeUntilExpiry < 5 * 60 * 1000;
        } catch (e) {
            console.error('Error parsing JWT token', e);
            return true;
        }
    }

    // Get token from cookie
    getTokenFromCookie(name) {
        const cookies = document.cookie.split(';');
        for (let i = 0; i < cookies.length; i++) {
            const cookie = cookies[i].trim();
            if (cookie.startsWith(name + '=')) {
                return cookie.substring(name.length + 1);
            }
        }
        return null;
    }

    // Refresh the access token
    async refreshToken() {
        if (this.tokenRefreshInProgress) return;
        this.tokenRefreshInProgress = true;
        
        try {
            const response = await fetch('/api/auth/refresh', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                credentials: 'same-origin'
            });
            
            const data = await response.json();
            
            if (!data.success) {
                console.error('Token refresh failed:', data.message);
                // If refresh fails, redirect to login
                window.location.href = '/signin';
            } else {
                console.log('Token refreshed successfully');
            }
        } catch (error) {
            console.error('Error refreshing token:', error);
        } finally {
            this.tokenRefreshInProgress = false;
        }
    }

    // Setup automatic token refresh
    setupTokenRefresh() {
        // Check token status every minute
        setInterval(async () => {
            if (this.isTokenExpiringSoon()) {
                await this.refreshToken();
            }
        }, 60000); // Check every minute
        
        // Also refresh token on page load if needed
        if (this.isTokenExpiringSoon()) {
            this.refreshToken();
        }
    }
}

// Initialize auth handler when page loads
document.addEventListener('DOMContentLoaded', () => {
    window.authHandler = new AuthHandler();
});
