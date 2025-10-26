// GSAP animations and small UX helpers
// Requires gsap and ScrollTrigger (loaded via CDN in HTML)

(function () {
  const ready = (fn) => (document.readyState !== 'loading' ? fn() : document.addEventListener('DOMContentLoaded', fn));

  ready(() => {
    if (window.gsap) {
      // Register plugin if available
      if (window.ScrollTrigger) gsap.registerPlugin(ScrollTrigger);

      // Hero animations
      const tl = gsap.timeline({ defaults: { ease: 'power2.out', duration: 0.8 } });
      tl.from('.hero-title', { y: 24, opacity: 0 })
        .from('.hero-subtitle', { y: 16, opacity: 0 }, '-=0.4')
        .from('.cta-row .btn-primary, .cta-row .btn-ghost', { y: 10, opacity: 0, stagger: 0.12 }, '-=0.4');

      // Smooth scrolling (if plugin is available)
      if (window.ScrollSmoother) {
        gsap.registerPlugin(ScrollSmoother);
        ScrollSmoother.create({
          wrapper: '#smooth-wrapper',
          content: '#smooth-content',
          smooth: 1.5,
          effects: true,
        });
      }

      // Column-wise feature cards with a pinned, scrubbed timeline for ultra-smooth animation
      const cards = gsap.utils.toArray('.feature-card');
      gsap.set(cards, { autoAlpha: 0, y: 64, scale: 0.92, transformOrigin: 'center center' });
      if (cards.length) {
        const tl = gsap.timeline({
          scrollTrigger: {
            trigger: '.features',
            start: 'top top',
            end: () => '+=' + (window.innerHeight * (cards.length + 1.2)),
            scrub: 0.75,
            pin: true,
            anticipatePin: 1,
            snap: 1 / (cards.length * 1.2),
          }
        });
        const step = 1.1; // spacing between card animations
        cards.forEach((card, i) => {
          // Fade/scale in
          tl.to(card, { autoAlpha: 1, y: 0, scale: 1.08, duration: 0.7, ease: 'power3.out' }, i * step);
          // For all but the last card, ease back to scale 1
          if (i < cards.length - 1) {
            tl.to(card, { scale: 1, boxShadow: '0 16px 36px rgba(0,0,0,0.35)', duration: 0.6, ease: 'power2.out' }, i * step + 0.55);
          }
        });
        // Give the last card extra time enlarged, then gently settle back
        const last = cards[cards.length - 1];
        tl.to(last, { scale: 1.08, duration: 0.3, ease: 'power1.out' }, "+=0.4")
          .to(last, { scale: 1, boxShadow: '0 16px 36px rgba(0,0,0,0.35)', duration: 0.6, ease: 'power2.out' });
      }

      // Auth card entrance
      gsap.from('.auth-card', { y: 20, opacity: 0, duration: 0.6, ease: 'power2.out' });
    }

    // Signup page: toggle volunteer fields
    const userType = document.getElementById('userType');
    const volunteerFields = document.getElementById('volunteerFields');
    const maxDistanceField = document.getElementById('maxDistance');

    if (userType && volunteerFields && maxDistanceField) {
      const update = () => {
        const isVolunteer = userType.value === 'VOLUNTEER';
        volunteerFields.style.display = isVolunteer ? 'block' : 'none';
        maxDistanceField.required = isVolunteer;
        if (!isVolunteer) maxDistanceField.value = '';
      };
      userType.addEventListener('change', update);
      update();
    }
  });
})();
