// Chatbot Module JavaScript - Connected to Google Gemini API

// Message storage
let messages = [
    {
        id: '1',
        text: "Hello! I'm your MindCare AI assistant. I'm connected to Google AI to help you better. How can I assist you today?",
        sender: 'bot',
        timestamp: new Date(),
        suggestions: [
            'Help with stress management',
            'Find learning modules',
            'Book a counseling session',
            'Tell me about self-assessments'
        ]
    }
];

// Initialize on page load
document.addEventListener('DOMContentLoaded', function () {
    renderMessages();

    // Add enter key listener to input
    const messageInput = document.getElementById('message-input');
    if(messageInput){
        messageInput.addEventListener('keypress', function (e) {
            if (e.key === 'Enter') {
                sendMessage();
            }
        });
    }
});

// Send a message
function sendMessage() {
    const messageInput = document.getElementById('message-input');
    const messageText = messageInput.value.trim();

    if (!messageText) return;

    // 1. Add User Message to UI
    const userMessage = {
        id: Date.now().toString(),
        text: messageText,
        sender: 'user',
        timestamp: new Date(),
        suggestions: []
    };

    messages.push(userMessage);
    messageInput.value = ''; // Clear input
    renderMessages();

    // 2. Send to Java Backend (Gemini API)
    // We show a "Thinking..." indicator by adding a temporary loading message if desired, 
    // but for now, we will just wait for the response.
    
    fetch('/api/chat/send', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ message: messageText })
    })
    .then(response => response.json())
    .then(data => {
        // 3. Create Bot Message from API Data
        const botMessage = {
            id: (Date.now() + 1).toString(),
            text: data.response, // This comes from Gemini
            sender: 'bot',
            timestamp: new Date(),
            suggestions: [] // You can add static suggestions here if you want
        };

        messages.push(botMessage);
        renderMessages();
    })
    .catch(error => {
        console.error('Error:', error);
        const errorMessage = {
            id: (Date.now() + 1).toString(),
            text: "I'm having trouble connecting to the server. Please try again.",
            sender: 'bot',
            timestamp: new Date(),
            suggestions: []
        };
        messages.push(errorMessage);
        renderMessages();
    });
}

// Quick action handler
function quickAction(topic) {
    const messageInput = document.getElementById('message-input');
    let message = '';
    
    // We convert the topic button clicks into natural language questions for the AI
    switch (topic) {
        case 'stress': message = 'I need help with stress management.'; break;
        case 'module': message = 'What learning modules are available?'; break;
        case 'counseling': message = 'How do I book a counseling session?'; break;
        case 'assessment': message = 'Tell me about self-assessments.'; break;
        case 'anxiety': message = 'I need help with anxiety.'; break;
        case 'sleep': message = 'I am having trouble sleeping.'; break;
        case 'mindfulness': message = 'Tell me about mindfulness.'; break;
        default: message = topic;
    }

    messageInput.value = message;
    sendMessage();
}

// Handle suggestion click
function handleSuggestion(suggestion) {
    const messageInput = document.getElementById('message-input');
    messageInput.value = suggestion;
    sendMessage();
}

// Render all messages (Kept exactly the same as your original file to preserve styling)
function renderMessages() {
    const container = document.getElementById('chat-messages');
    if(!container) return;
    
    container.innerHTML = '';

    messages.forEach(function (message) {
        const messageDiv = document.createElement('div');
        messageDiv.className = `d-flex mb-3 ${message.sender === 'user' ? 'justify-content-end' : 'justify-content-start'}`;

        const messageContent = document.createElement('div');
        messageContent.className = `d-flex gap-3 ${message.sender === 'user' ? 'flex-row-reverse' : 'flex-row'}`;
        messageContent.style.maxWidth = '80%';

        // Avatar
        const avatar = document.createElement('div');
        avatar.className = 'rounded-circle d-flex align-items-center justify-content-center flex-shrink-0';
        avatar.style.width = '35px';
        avatar.style.height = '35px';

        if (message.sender === 'user') {
            avatar.style.background = '#3b82f6';
            avatar.innerHTML = '<i class="fas fa-user text-white small"></i>';
        } else {
            avatar.style.background = 'linear-gradient(135deg, #3b82f6, #22c55e)';
            avatar.innerHTML = '<i class="fas fa-robot text-white small"></i>';
        }

        // Message bubble and content
        const bubbleContainer = document.createElement('div');

        const bubble = document.createElement('div');
        bubble.className = 'p-3 rounded-3 shadow-sm mb-1';

        if (message.sender === 'user') {
            bubble.style.background = 'linear-gradient(to right, #3b82f6, #22c55e)';
            bubble.style.color = 'white';
        } else {
            bubble.style.background = 'white';
            bubble.style.color = '#1e3a8a';
        }

        const messageText = document.createElement('p');
        messageText.className = 'mb-0';
        messageText.style.whiteSpace = 'pre-line';
        // Parse simple markdown bolding if Gemini sends it (**text**)
        let formattedText = message.text.replace(/\*\*(.*?)\*\*/g, '<b>$1</b>');
        messageText.innerHTML = formattedText; 
        
        bubble.appendChild(messageText);
        bubbleContainer.appendChild(bubble);

        // Suggestions
        if (message.suggestions && message.suggestions.length > 0) {
            const suggestionsDiv = document.createElement('div');
            suggestionsDiv.className = 'd-flex flex-wrap gap-2 mb-2';

            message.suggestions.forEach(function (suggestion) {
                const suggestionBtn = document.createElement('button');
                suggestionBtn.className = 'btn btn-sm btn-outline-primary rounded-pill';
                suggestionBtn.textContent = suggestion;
                suggestionBtn.onclick = function () {
                    handleSuggestion(suggestion);
                };
                suggestionsDiv.appendChild(suggestionBtn);
            });

            bubbleContainer.appendChild(suggestionsDiv);
        }

        // Timestamp
        const timestamp = document.createElement('small');
        timestamp.className = 'text-muted px-2';
        timestamp.textContent = formatTime(message.timestamp);
        bubbleContainer.appendChild(timestamp);

        messageContent.appendChild(avatar);
        messageContent.appendChild(bubbleContainer);
        messageDiv.appendChild(messageContent);
        container.appendChild(messageDiv);
    });

    // Scroll to bottom
    container.scrollTop = container.scrollHeight;
}

// Format timestamp
function formatTime(date) {
    if(!date) return '';
    // Ensure date is a Date object (in case it came from JSON as string)
    const d = new Date(date);
    const hours = d.getHours().toString().padStart(2, '0');
    const minutes = d.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
}