// Assessment Module JavaScript
// Handles state management and UI interactions for the self-assessment tool

// Assessment data
const assessments = {
    phq9: {
        id: 'phq9',
        title: 'PHQ-9 Depression Assessment',
        description: 'A 9-question screening tool for depression',
        questions: [
            {
                id: 1,
                text: 'Little interest or pleasure in doing things',
                options: [
                    { value: 0, label: 'Not at all' },
                    { value: 1, label: 'Several days' },
                    { value: 2, label: 'More than half the days' },
                    { value: 3, label: 'Nearly every day' }
                ]
            },
            {
                id: 2,
                text: 'Feeling down, depressed, or hopeless',
                options: [
                    { value: 0, label: 'Not at all' },
                    { value: 1, label: 'Several days' },
                    { value: 2, label: 'More than half the days' },
                    { value: 3, label: 'Nearly every day' }
                ]
            },
            {
                id: 3,
                text: 'Trouble falling or staying asleep, or sleeping too much',
                options: [
                    { value: 0, label: 'Not at all' },
                    { value: 1, label: 'Several days' },
                    { value: 2, label: 'More than half the days' },
                    { value: 3, label: 'Nearly every day' }
                ]
            },
            {
                id: 4,
                text: 'Feeling tired or having little energy',
                options: [
                    { value: 0, label: 'Not at all' },
                    { value: 1, label: 'Several days' },
                    { value: 2, label: 'More than half the days' },
                    { value: 3, label: 'Nearly every day' }
                ]
            },
            {
                id: 5,
                text: 'Poor appetite or overeating',
                options: [
                    { value: 0, label: 'Not at all' },
                    { value: 1, label: 'Several days' },
                    { value: 2, label: 'More than half the days' },
                    { value: 3, label: 'Nearly every day' }
                ]
            }
        ]
    },
    gad7: {
        id: 'gad7',
        title: 'GAD-7 Anxiety Assessment',
        description: 'A 7-question screening tool for generalized anxiety disorder',
        questions: [
            {
                id: 1,
                text: 'Feeling nervous, anxious, or on edge',
                options: [
                    { value: 0, label: 'Not at all' },
                    { value: 1, label: 'Several days' },
                    { value: 2, label: 'More than half the days' },
                    { value: 3, label: 'Nearly every day' }
                ]
            },
            {
                id: 2,
                text: 'Not being able to stop or control worrying',
                options: [
                    { value: 0, label: 'Not at all' },
                    { value: 1, label: 'Several days' },
                    { value: 2, label: 'More than half the days' },
                    { value: 3, label: 'Nearly every day' }
                ]
            },
            {
                id: 3,
                text: 'Worrying too much about different things',
                options: [
                    { value: 0, label: 'Not at all' },
                    { value: 1, label: 'Several days' },
                    { value: 2, label: 'More than half the days' },
                    { value: 3, label: 'Nearly every day' }
                ]
            },
            {
                id: 4,
                text: 'Trouble relaxing',
                options: [
                    { value: 0, label: 'Not at all' },
                    { value: 1, label: 'Several days' },
                    { value: 2, label: 'More than half the days' },
                    { value: 3, label: 'Nearly every day' }
                ]
            }
        ]
    }
};

// State management
let currentAssessment = null;
let currentQuestionIndex = 0;
let answers = {};
let pastResults = [
    {
        id: '1',
        title: 'PHQ-9 Depression Assessment',
        score: 8,
        maxScore: 15,
        severity: 'mild',
        date: '2024-11-01',
        recommendations: [
            'Consider talking to a counselor',
            'Practice stress management techniques',
            'Maintain a regular sleep schedule'
        ]
    }
];

// Initialize on page load
document.addEventListener('DOMContentLoaded', function() {
    renderAssessmentHistory();
});

// Start an assessment
function startAssessment(assessmentId) {
    currentAssessment = assessments[assessmentId];
    currentQuestionIndex = 0;
    answers = {};
    
    // Hide selection view, show question view
    document.getElementById('assessment-selection-view').style.display = 'none';
    document.getElementById('assessment-question-view').style.display = 'block';
    document.getElementById('assessment-results-view').style.display = 'none';
    
    renderQuestion();
}

// Render current question
function renderQuestion() {
    const question = currentAssessment.questions[currentQuestionIndex];
    const progress = ((currentQuestionIndex + 1) / currentAssessment.questions.length) * 100;
    
    // Update header
    document.getElementById('assessment-title').textContent = currentAssessment.title;
    document.getElementById('question-counter').textContent = 
        `Question ${currentQuestionIndex + 1} of ${currentAssessment.questions.length}`;
    
    // Update progress bar
    document.getElementById('progress-bar').style.width = progress + '%';
    
    // Update question text
    document.getElementById('question-text').textContent = question.text;
    
    // Render options
    const optionsContainer = document.getElementById('question-options');
    optionsContainer.innerHTML = '';
    
    question.options.forEach(option => {
        const optionDiv = document.createElement('div');
        optionDiv.className = 'p-3 rounded-3 border mb-2 assessment-option';
        optionDiv.style.cursor = 'pointer';
        optionDiv.style.transition = 'all 0.2s ease';
        
        const isSelected = answers[question.id] === option.value;
        if (isSelected) {
            optionDiv.style.backgroundColor = '#eff6ff';
            optionDiv.style.borderColor = '#3b82f6';
        }
        
        optionDiv.innerHTML = `
            <div class="d-flex align-items-center gap-3">
                <input type="radio" 
                       name="question-${question.id}" 
                       value="${option.value}" 
                       id="option-${question.id}-${option.value}"
                       ${isSelected ? 'checked' : ''}
                       style="width: 1.2em; height: 1.2em; cursor: pointer;">
                <label for="option-${question.id}-${option.value}" 
                       class="mb-0 flex-grow-1" 
                       style="cursor: pointer;">
                    ${option.label}
                </label>
            </div>
        `;
        
        optionDiv.addEventListener('click', function() {
            handleAnswer(question.id, option.value);
        });
        
        optionsContainer.appendChild(optionDiv);
    });
    
    // Update button states
    updateButtonStates();
}

// Handle answer selection
function handleAnswer(questionId, value) {
    answers[questionId] = value;
    renderQuestion(); // Re-render to show selection
}

// Update button states
function updateButtonStates() {
    const question = currentAssessment.questions[currentQuestionIndex];
    const hasAnswer = answers[question.id] !== undefined;
    
    // Previous button
    const btnPrevious = document.getElementById('btn-previous');
    btnPrevious.disabled = currentQuestionIndex === 0;
    
    // Next button
    const btnNext = document.getElementById('btn-next');
    btnNext.disabled = !hasAnswer;
    
    // Update next button text
    const isLastQuestion = currentQuestionIndex === currentAssessment.questions.length - 1;
    document.getElementById('next-btn-text').textContent = isLastQuestion ? 'Submit' : 'Next';
}

// Navigate to previous question
function previousQuestion() {
    if (currentQuestionIndex > 0) {
        currentQuestionIndex--;
        renderQuestion();
    }
}

// Navigate to next question or submit
function nextQuestion() {
    const question = currentAssessment.questions[currentQuestionIndex];
    if (answers[question.id] === undefined) return;
    
    if (currentQuestionIndex < currentAssessment.questions.length - 1) {
        currentQuestionIndex++;
        renderQuestion();
    } else {
        submitAssessment();
    }
}

// Submit assessment and show results
function submitAssessment() {
    // Calculate score
    const totalScore = Object.values(answers).reduce((sum, val) => sum + val, 0);
    const maxScore = currentAssessment.questions.length * 3;
    const percentage = (totalScore / maxScore) * 100;
    
    // Determine severity
    let severity, severityClass, severityIcon;
    if (percentage < 25) {
        severity = 'minimal';
        severityClass = 'bg-success text-white';
        severityIcon = 'fa-arrow-down';
    } else if (percentage < 50) {
        severity = 'mild';
        severityClass = 'bg-info text-white';
        severityIcon = 'fa-minus';
    } else if (percentage < 75) {
        severity = 'moderate';
        severityClass = 'bg-warning text-dark';
        severityIcon = 'fa-arrow-up';
    } else {
        severity = 'severe';
        severityClass = 'bg-danger text-white';
        severityIcon = 'fa-arrow-up';
    }
    
    // Create result object
    const result = {
        id: Date.now().toString(),
        title: currentAssessment.title,
        score: totalScore,
        maxScore: maxScore,
        severity: severity,
        date: new Date().toISOString().split('T')[0],
        recommendations: [
            'Continue monitoring your mental health',
            'Consider booking a counseling session',
            'Practice the recommended coping strategies'
        ]
    };
    
    // Add to history
    pastResults.unshift(result);
    
    // Show results view
    document.getElementById('assessment-selection-view').style.display = 'none';
    document.getElementById('assessment-question-view').style.display = 'none';
    document.getElementById('assessment-results-view').style.display = 'block';
    
    // Render results
    document.getElementById('result-title').textContent = result.title;
    document.getElementById('result-score').textContent = `${result.score}/${result.maxScore}`;
    
    const severityBadge = document.getElementById('result-severity-badge');
    severityBadge.className = `badge px-4 py-2 fs-6 ${severityClass}`;
    severityBadge.innerHTML = `<i class="fas ${severityIcon} me-2"></i>${severity.charAt(0).toUpperCase() + severity.slice(1)}`;
    
    // Render recommendations
    const recommendationsContainer = document.getElementById('result-recommendations');
    recommendationsContainer.innerHTML = '';
    result.recommendations.forEach(rec => {
        const recDiv = document.createElement('div');
        recDiv.className = 'd-flex align-items-start gap-3 p-3 rounded-3 mb-2';
        recDiv.style.backgroundColor = '#eff6ff';
        recDiv.innerHTML = `
            <i class="fas fa-check-circle text-primary mt-1"></i>
            <p class="mb-0" style="color: #1e3a8a;">${rec}</p>
        `;
        recommendationsContainer.appendChild(recDiv);
    });
    
    // Update history
    renderAssessmentHistory();
}

// Go back to assessments list
function backToAssessments() {
    document.getElementById('assessment-selection-view').style.display = 'block';
    document.getElementById('assessment-question-view').style.display = 'none';
    document.getElementById('assessment-results-view').style.display = 'none';
    
    currentAssessment = null;
    currentQuestionIndex = 0;
    answers = {};
}

// Render assessment history
function renderAssessmentHistory() {
    const historyContainer = document.getElementById('assessment-history');
    
    if (pastResults.length === 0) {
        historyContainer.innerHTML = '<p class="text-center text-muted py-4">No assessments completed yet</p>';
        return;
    }
    
    historyContainer.innerHTML = '';
    pastResults.forEach(result => {
        const percentage = (result.score / result.maxScore) * 100;
        let severityClass, severityIcon;
        
        if (result.severity === 'minimal') {
            severityClass = 'bg-success text-white';
            severityIcon = 'fa-arrow-down';
        } else if (result.severity === 'mild') {
            severityClass = 'bg-info text-white';
            severityIcon = 'fa-minus';
        } else if (result.severity === 'moderate') {
            severityClass = 'bg-warning text-dark';
            severityIcon = 'fa-arrow-up';
        } else {
            severityClass = 'bg-danger text-white';
            severityIcon = 'fa-arrow-up';
        }
        
        const historyItem = document.createElement('div');
        historyItem.className = 'p-3 rounded-3 border mb-3';
        historyItem.style.backgroundColor = '#f8fafc';
        historyItem.innerHTML = `
            <div class="d-flex justify-content-between align-items-center mb-2">
                <h6 class="fw-bold mb-0" style="color: #1e3a8a;">${result.title}</h6>
                <small class="text-muted">${result.date}</small>
            </div>
            <div class="d-flex align-items-center gap-3">
                <div class="flex-grow-1">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <small style="color: #2563eb;">Score: ${result.score}/${result.maxScore}</small>
                        <span class="badge ${severityClass}">
                            <i class="fas ${severityIcon} me-1"></i>${result.severity.charAt(0).toUpperCase() + result.severity.slice(1)}
                        </span>
                    </div>
                    <div class="progress" style="height: 6px;">
                        <div class="progress-bar" style="width: ${percentage}%; background: linear-gradient(to right, #3b82f6, #22c55e);"></div>
                    </div>
                </div>
            </div>
        `;
        historyContainer.appendChild(historyItem);
    });
}
