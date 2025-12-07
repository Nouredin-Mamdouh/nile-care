// Chatbot Module JavaScript
// Handles message display, user input, and bot responses

// Message storage
let messages = [
    {
        id: '1',
        text: "Hello! I'm your MindCare AI assistant. I'm here to help you with mental health resources, learning modules, and general support. How can I assist you today?",
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

// Predefined bot responses
const predefinedResponses = {
    'stress': "I understand you're dealing with stress. We have excellent resources to help! I recommend starting with our 'Stress Management Fundamentals' module. It covers breathing exercises, time management, and cognitive strategies. Would you like me to guide you there?",
    'anxiety': "Anxiety can be challenging. Our 'Mindfulness & Meditation' module has proven techniques to help manage anxiety. We also offer the GAD-7 assessment to help you understand your anxiety levels better. What would you like to explore first?",
    'sleep': "Sleep is crucial for mental health! Check out our 'Sleep Hygiene & Rest' module for practical strategies to improve your sleep patterns. It includes tips on creating a sleep-friendly environment and establishing healthy routines.",
    'module': "We offer several learning modules covering topics like stress management, mindfulness, emotional regulation, sleep hygiene, and building healthy relationships. Each module includes videos, articles, and practical exercises. Which topic interests you most?",
    'counseling': "Booking a counseling session is easy! Navigate to the 'Counseling' section from the sidebar. You can view our professional counselors' profiles, check their availability, and book a session that fits your schedule. Would you like me to explain more about our counselors?",
    'assessment': "We offer validated self-assessment tools including PHQ-9 for depression and GAD-7 for anxiety. These tools can help you understand your current mental health status. The assessments are private, and results come with personalized recommendations. Would you like to take one?",
    'help': "I can help you with:\n• Finding and navigating learning modules\n• Understanding self-assessments\n• Booking counseling appointments\n• Getting mental health tips and resources\n• Answering questions about the platform\n\nWhat would you like to know more about?",
    'mindfulness': "Mindfulness is a powerful tool for mental wellbeing. Our platform offers guided meditation exercises, breathing techniques, and mindfulness practices. These can help reduce stress, improve focus, and enhance emotional regulation. Would you like to start with a beginner's guide?"
};

// Initialize on page load
document.addEventListener('DOMContentLoaded', function () {
    renderMessages();

    // Add enter key listener to input
    const messageInput = document.getElementById('message-input');
    messageInput.addEventListener('keypress', function (e) {
        if (e.key === 'Enter') {
            sendMessage();
        }
    });
});

// Get bot response based on user message
function getBotResponse(userMessage) {
    const lowerMessage = userMessage.toLowerCase();

    // Check for keyword matches
    for (const [key, response] of Object.entries(predefinedResponses)) {
        if (lowerMessage.includes(key)) {
            return {
                text: response,
                suggestions: key === 'help' ? [] : ['Tell me more', 'What else can you help with?', 'Book counseling']
            };
        }
    }

    // Default response
    return {
        text: "I'm here to help! You can ask me about learning modules, counseling sessions, self-assessments, or general mental health support. What would you like to know?",
        suggestions: ['Show me modules', 'How to book counseling', 'Mental health tips', 'Take an assessment']
    };
}

// Send a message
function sendMessage() {
    const messageInput = document.getElementById('message-input');
    const messageText = messageInput.value.trim();

    if (!messageText) return;

    // Add user message
    const userMessage = {
        id: Date.now().toString(),
        text: messageText,
        sender: 'user',
        timestamp: new Date(),
        suggestions: []
    };

    messages.push(userMessage);
    messageInput.value = '';
    renderMessages();

    // Simulate bot typing and response
    setTimeout(function () {
        const botResponse = getBotResponse(messageText);
        const botMessage = {
            id: (Date.now() + 1).toString(),
            text: botResponse.text,
            sender: 'bot',
            timestamp: new Date(),
            suggestions: botResponse.suggestions
        };

        messages.push(botMessage);
        renderMessages();
    }, 1000);
}

// Quick action handler
function quickAction(topic) {
    const messageInput = document.getElementById('message-input');

    let message = '';
    switch (topic) {
        case 'stress':
            message = 'Help with stress management';
            break;
        case 'module':
            message = 'What learning modules are available?';
            break;
        case 'counseling':
            message = 'How do I book a counseling session?';
            break;
        case 'assessment':
            message = 'Tell me about self-assessments';
            break;
        case 'anxiety':
            message = 'I need help with anxiety';
            break;
        case 'sleep':
            message = 'Help me with sleep issues';
            break;
        case 'mindfulness':
            message = 'Tell me about mindfulness';
            break;
        default:
            message = topic;
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

// Render all messages
function renderMessages() {
    const container = document.getElementById('chat-messages');
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
        messageText.textContent = message.text;
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
    const hours = date.getHours().toString().padStart(2, '0');
    const minutes = date.getMinutes().toString().padStart(2, '0');
    return `${hours}:${minutes}`;
}
