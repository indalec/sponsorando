let stripe;
let card;

fetch('/api/stripe-public-key')
    .then(response => response.text())
    .then(publicKey => {
        if (!publicKey || publicKey.trim() === '') {
            throw new Error('Invalid public key received');
        }
        stripe = Stripe(publicKey.trim());
        const elements = stripe.elements({locale: 'en'});
        card = elements.create('card', {
            style: {
                base: {
                    fontSize: '16px',
                    color: '#32325d',
                    '::placeholder': {
                        color: '#aab7c4',
                    },
                },
            },
            autocomplete: 'off',
        });
        card.mount('#card-element');
    })
    .catch(error => {
        console.error('Error fetching or using Stripe public key:', error);
        document.getElementById('card-errors').textContent = 'There was an error setting up the payment form. Please try again later.';
    });

const form = document.getElementById('donation-form');
form.addEventListener('submit', async function(event) {
    event.preventDefault();
    form.classList.add('was-validated');

    if (!validateForm()) {
        return;
    }

    try {
        const {token, error} = await stripe.createToken(card);

        if (error) {
            throw new Error(error.message);
        }
        await submitPayment(token);
    } catch (error) {
        console.error('Payment processing error:', error);
        showError(error.message || 'Payment processing failed. Please try again.');
    }
});

function validateForm() {
    const amount = getSelectedAmount();
    const currency = document.getElementById('currency').value;

    if (!amount || isNaN(amount) || amount <= 0) {
        showError('Please enter a valid amount.');
        return false;
    }

    if (!currency) {
        showError('Please select a currency.');
        return false;
    }

    return true;
}

function getSelectedAmount() {
    const selectedAmount = document.querySelector('input[name="donationAmount"]:checked');
    if (selectedAmount && selectedAmount.value !== 'custom') {
        return parseFloat(selectedAmount.value);
    }
    return parseFloat(document.getElementById('customAmount').value);
}

async function submitPayment(token) {
    const formData = new FormData(form);
    formData.append('stripeToken', token.id);
    formData.append('amount', getSelectedAmount());

    try {
        const response = await fetch(form.action, {
            method: 'POST',
            body: formData
        });

        const result = await response.json();

        if (response.ok && result.success) {
            showSuccessMessage(result.message);
            setTimeout(() => {
                window.location.href = result.redirectUrl;
            }, 2000);
        } else {
            showError(result.message || 'Payment failed. Please try again.');
        }
    } catch (error) {
        console.error('Submission error:', error);
        showError('An error occurred. Please try again.');
    }
}

function showSuccessMessage(message) {
    const successElement = document.getElementById('success-message');
    successElement.textContent = message;
    successElement.style.display = 'block';
}

function showError(message) {
    const errorElement = document.getElementById('card-errors');
    errorElement.textContent = message;
    errorElement.style.display = 'block';
}

document.addEventListener('DOMContentLoaded', function() {
    const currencySelect = document.getElementById('currency');
    const currencySymbols = document.querySelectorAll('.currency-symbol');
    const customOption = document.getElementById('customOption');
    const customAmountInputGroup = document.getElementById('customAmountInputGroup');
    const donationOptions = document.querySelectorAll('input[name="donationAmount"]');

    function updateCurrencySymbol() {
        const selectedOption = currencySelect.options[currencySelect.selectedIndex];
        const symbol = selectedOption.dataset.symbol || selectedOption.value;
        currencySymbols.forEach(el => el.textContent = symbol);
    }

    currencySelect.addEventListener('change', updateCurrencySymbol);
    updateCurrencySymbol();

    donationOptions.forEach(option => {
        option.addEventListener('change', function() {
            if (this.id === 'customOption') {
                customAmountInputGroup.style.display = 'inline-block';
            } else {
                customAmountInputGroup.style.display = 'none';
            }
        });
    });
});
