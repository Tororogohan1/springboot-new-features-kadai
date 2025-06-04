const stripe = Stripe('pk_test_51QDOsUKT3o6xVyp0Kwzt1jDnlUAS6zB5PCkm7APM98OTUSF9ksforHbNLLvp75nS00KA44GA6RJZwaykGKn6yWTz003Yp9Z7co');
const paymentButton = document.querySelector('#paymentButton');

paymentButton.addEventListener('click', () => {
 stripe.redirectToCheckout({
   sessionId: sessionId
 })
});