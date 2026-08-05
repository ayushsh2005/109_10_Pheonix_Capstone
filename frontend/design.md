# Premium Product Design & Interaction Design System

## Role

You are a world-class product designer specializing in futuristic SaaS,
consumer applications, fintech platforms, AI products, and high-end
digital experiences.

Your responsibility is not only to create screens.

Your responsibility is to create a complete product experience.

Every design decision must feel:

-   Premium
-   Minimal
-   Intelligent
-   Trustworthy
-   Emotionally engaging

------------------------------------------------------------------------

# Core Design Direction

The product MUST follow:

## Visual Language

-   Minimalist aesthetic
-   Glassmorphism-inspired visual language
-   Premium spatial composition
-   Modern typography hierarchy
-   Soft depth
-   Layered surfaces
-   Subtle transparency
-   Elegant gradients
-   Calm visual rhythm

The experience should feel comparable to:

-   Apple design philosophy
-   VisionOS spatial interfaces
-   High-end fintech platforms
-   Premium AI products
-   Modern luxury digital brands

Avoid clutter. Every element must have purpose.

------------------------------------------------------------------------

# HSBC Inspired Brand System

## Primary Colors

### HSBC Red

`#E60000`

Usage: - Primary actions - Important highlights - Brand moments -
Financial indicators

### Deep Black

`#000000`

Usage: - Primary typography - Premium dark surfaces

### Neutral Grey

`#898D8D`

Usage: - Secondary text - Metadata - Borders - Supporting information

### Pure White

`#FFFFFF`

Usage: - Backgrounds - Cards - Glass surfaces

------------------------------------------------------------------------

# Glassmorphism System

Implement a sophisticated glass UI system.

Use:

-   Frosted glass surfaces
-   Background blur effects
-   Semi-transparent layers
-   Subtle gradients
-   Soft shadows
-   Fine borders
-   Depth-based elevation

Glass components must adapt based on:

-   Theme mode
-   Background brightness
-   Accessibility requirements

Avoid excessive blur.

Glass should enhance hierarchy, not reduce readability.

------------------------------------------------------------------------

# Theme Responsive Architecture

Support:

-   Light mode
-   Dark mode
-   System theme

## Light Mode

Characteristics:

-   Premium whites
-   Soft neutral surfaces
-   Elegant contrast
-   Minimal visual noise

## Dark Mode

Characteristics:

-   Deep charcoal backgrounds
-   Controlled glow effects
-   Cinematic premium feeling
-   High readability

Every component must support:

-   Light variant
-   Dark variant
-   Hover state
-   Active state
-   Disabled state
-   Loading state
-   Error state

------------------------------------------------------------------------

# Micro Interaction Requirements

Every interaction should feel intentional, physical, and delightful.

## Navigation

Include:

-   Smooth page transitions using `View Transitions API` or CSS `@starting-style`
-   Active nav indicator slides between items with spring physics (stiffness 300, damping 30)
-   Sidebar collapse/expand with cubic-bezier(0.34, 1.56, 0.64, 1) overshoot
-   Breadcrumb items fade-and-slide in sequentially on route change
-   Hover state: nav items shift 2px right with 150ms ease-out
-   Active page item glows with a 3px left border that animates width 0→3px in 200ms

## Buttons

Include:

-   Rest → Hover: translateY(-2px) + box-shadow depth increase in 150ms ease-out
-   Hover → Press: translateY(0) + box-shadow flatten in 80ms ease-in (compression feel)
-   Ripple: radial scale from click origin, opacity 0.15→0 over 400ms
-   Loading: text fades out (200ms), spinner fades in (200ms), width locks to prevent reflow
-   Success: spinner morphs into checkmark via path animation, green flash 300ms, then resets
-   Danger button: subtle pulse on first render to draw attention (one-time, 600ms)
-   Disabled: opacity transitions to 0.4 over 150ms, cursor change is immediate

## Cards

Include:

-   Hover lift: translateY(-4px) + shadow deepens from `0 2px 8px` → `0 12px 32px` in 200ms ease
-   Click press: translateY(-1px) + shadow mid-state 80ms
-   Entry animation: cards stagger-fade-in with translateY(16px)→0, opacity 0→1, 50ms stagger per card
-   Stat cards: numerical values count-up from 0 on first load (600ms ease-out, RAF-based)
-   Glassmorphism backdrop-filter blur transitions smoothly on theme change (300ms)
-   Border shimmer on hover: a 1.5px gradient border sweeps clockwise in 800ms

## Forms

Include:

-   Floating labels: translate(-50%, -160%) + scale(0.85) + color shift on focus, 200ms ease
-   Input focus: border-color transitions + a soft glow (box-shadow 0 0 0 3px rgba(primary, 0.15)) in 150ms
-   Validation inline: error message slides down from translateY(-8px) + opacity 0→1 in 200ms ease-out
-   Error shake: horizontal keyframe shake (±4px, 3 cycles) at 300ms on failed submit
-   Success state: border transitions to green + checkmark icon fades in from right in 250ms
-   Character counter: opacity 0→1 on focus, fades out when field is empty and blurred
-   Dropdown open: list expands from scaleY(0)→scaleY(1) with transform-origin top in 180ms

## Loading Experience

Avoid generic loaders.

Create:

-   Skeleton screens: shimmer wave sweeps left→right with 1.5s infinite, uses CSS `@keyframes`
-   Progressive content reveal: skeleton → real content with cross-fade (300ms ease)
-   Chart loading: axes and grid draw-in first (300ms), then data line/arc animates in (500ms ease-out)
-   Table rows: stagger fade-in 30ms apart, translateY(8px)→0
-   Page-level: top progress bar (NProgress-style) pulses width 0→70% then snaps to 100% on complete
-   Lazy image loads: blur(8px)→blur(0) + scale(1.05)→scale(1) as image loads

## Data Visualizations

-   Line/area charts: path draws in left→right using `stroke-dashoffset` animation (800ms ease-out)
-   Pie/donut charts: each segment rotates in from 0deg with 60ms stagger (400ms total)
-   Bar charts: bars grow from baseline height 0→final with 400ms ease-out, staggered 40ms
-   Number counters on stat cards: count-up using `requestAnimationFrame` with easeOutExpo curve
-   Chart hover tooltips: scale(0.95)→scale(1) + opacity 0→1 in 120ms, positioned with smart edge detection
-   Y-axis labels: fade in after chart renders (200ms delay, 200ms duration)

## Modals & Dialogs

-   Open: backdrop fades in (200ms) + dialog scales from scale(0.95)→scale(1) + opacity 0→1 (250ms ease-out)
-   Close: reverse — dialog scale(1)→scale(0.97) + opacity fade (180ms ease-in), backdrop fades (150ms)
-   Confirm dialog: AlertTriangle icon bounces once on entry with scale(1.2)→scale(1) (300ms)
-   Shake on invalid confirm: same horizontal shake as form error
-   Backdrop click: brief scale(1.01) pulse on dialog to indicate "you must act here" before closing

## Notifications (Toast)

-   Entry: slide in from right with translateX(110%)→translateX(0) + opacity 0→1, 300ms cubic-bezier(0.34,1.56,0.64,1) (slight overshoot)
-   Hover pause: progress bar pauses on hover
-   Auto-dismiss: progress bar depletes left→right over the lifetime duration
-   Exit: translateX(0)→translateX(110%) + opacity fade in 250ms ease-in
-   Stacking: each new toast pushes the previous up with 60ms spring animation
-   Icon entrance: icon scales 0→1 with 200ms delay after toast appears

## Table & List Interactions

-   Row hover: background transitions in 100ms, cursor pointer
-   Row click (clickable): brief flash-highlight (100ms) before navigation
-   Sortable column header: arrow icon rotates 0→180deg on direction change in 150ms
-   Delete animation: row slides out with height 0 + opacity fade in 250ms before data refresh
-   Newly added row: brief highlight pulse (success-bg color, 800ms fade-out) to confirm addition

## P&L & Financial Indicators

-   On load, P&L amount counts up from 0 to final value (600ms ease-out)
-   Color transitions: positive (green) / negative (red) cross-fades when value changes (300ms)
-   Percentage badge: scales in from scale(0.8)→scale(1) with opacity 200ms after main amount
-   Spark-line mini charts: stroke draw-in 600ms ease-out on mount
-   Range selector buttons: active indicator slides between options with 150ms ease

## Accessibility & Reduced Motion

All animations MUST respect:

```css
@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    transition-duration: 0.01ms !important;
  }
}
```

Provide equivalent static states for every animated element.

------------------------------------------------------------------------

# Motion Design System

Use:

-   Spring physics for spatial movement (overshoot on entrances)
-   Ease-out for elements entering the screen
-   Ease-in for elements leaving the screen
-   Linear for continuous/looping animations (shimmer, progress)
-   cubic-bezier(0.34, 1.56, 0.64, 1) for playful positive confirmations
-   cubic-bezier(0.4, 0, 0.2, 1) — Material "standard" — for most UI transitions

Timing:

| Category | Duration |
|---|---|
| Instant feedback (hover, focus) | 80–150ms |
| Micro interactions (button press, ripple) | 150–250ms |
| Component transitions (modal, dropdown) | 200–350ms |
| Page / major transitions | 300–500ms |
| Data animations (charts, counters) | 400–800ms |
| Skeleton → content reveal | 250–400ms |

Avoid:

-   Animations exceeding 800ms except data visualizations
-   Bouncing on destructive/error states (keep them decisive)
-   Staggered lists with more than 8 items animating (performance)
-   Animating layout-triggering properties (width/height) — use transform/opacity

------------------------------------------------------------------------

# Responsive Design Requirements

The design must adapt across:

## Mobile

-   One-handed usage
-   Thumb-friendly interaction zones
-   Compact navigation
-   Gesture support
-   Bottom navigation where appropriate

## Tablet

-   Adaptive layouts
-   Multi-column optimization
-   Better content density

## Desktop

-   Large canvas utilization
-   Advanced navigation patterns
-   Keyboard shortcuts support

## Large Screens

-   Maximum readable width
-   Balanced content scaling
-   Avoid stretched layouts

------------------------------------------------------------------------

# Portfolio Management Product Context

This is a premium investment management command center.

The product should communicate:

-   Financial intelligence
-   Trust
-   Control
-   Transparency
-   Professional decision making

Core experiences:

## Dashboard

Include:

-   Total customers
-   Assets managed
-   Portfolio value
-   Profit/loss
-   Asset allocation
-   Investment insights

## Customer Management

Do not create basic CRUD pages.

Use:

-   Customer profiles
-   Risk visualization
-   Portfolio summaries
-   Investment history

## Investment Management

Present:

-   Holdings
-   Asset allocation
-   Performance
-   Trends

## Investment Suggestions

Suggestions should feel intelligent and contextual.

------------------------------------------------------------------------

# Component Design Requirements

Create reusable components with:

-   Clear states
-   Variants
-   Accessibility support
-   Responsive behavior
-   Motion behavior

Required components:

-   Navigation system
-   Buttons
-   Inputs
-   Dropdowns
-   Cards
-   Modals
-   Dialogs
-   Tooltips
-   Tables
-   Empty states
-   Error states
-   Loading states
-   Notifications
-   Search experience
-   Filters
-   Data visualization components

------------------------------------------------------------------------

# Accessibility Standards

Follow:

-   WCAG AA compliance
-   Keyboard navigation
-   Screen reader support
-   Reduced motion preference
-   Proper contrast ratios
-   Focus visibility

Accessibility must be built into the design system.

------------------------------------------------------------------------

# Premium Details

Add:

-   Subtle gradients
-   Dynamic lighting effects
-   Contextual shadows
-   Soft reflections
-   Intelligent empty states
-   Beautiful onboarding moments
-   Delightful success moments

Every screen should answer:

"Why does this experience feel premium?"

------------------------------------------------------------------------

# Agent Rules

When generating UI:

ALWAYS:

-   Follow design tokens
-   Build reusable components
-   Support light and dark themes
-   Consider all states
-   Design for all devices
-   Add meaningful interactions
-   Prioritize clarity

NEVER:

-   Create generic admin dashboards
-   Use default bootstrap styling
-   Ignore mobile layouts
-   Ignore accessibility
-   Add unnecessary decoration
-   Sacrifice usability for visuals

------------------------------------------------------------------------

# Final Quality Bar

The final product should feel:

-   Minimal
-   Premium
-   Futuristic
-   Calm
-   Intelligent
-   Trustworthy
-   Effortlessly usable

Do not simply make screens.

Create an entire product experience.

------------------------------------------------------------------------

# India Region Requirements

## Currency

Use Indian Rupee exclusively.

Symbol: ₹ (U+20B9)

Locale: `en-IN`

Currency code: `INR`

Formatting examples:

-   ₹8,50,000 (8 Lakh 50 Thousand)
-   ₹1,25,00,000 (1 Crore 25 Lakh)
-   ₹14,50,00,000 (14 Crore 50 Lakh)

Never use £, $, €, or any other currency symbol.

## Indian Numbering System

Use Indian place-value notation:

-   Thousands: 1,000
-   Lakh: 1,00,000
-   Crore: 1,00,00,000

Chart Y-axis scale:

-   Below 1,00,000 → ₹K
-   1,00,000–99,99,999 → ₹L (Lakh)
-   1,00,00,000+ → ₹Cr (Crore)

## Phone Numbers

Format: +91 XXXXX XXXXX

Placeholder in forms: `+91 98765 43210`

Validation pattern: `+91` country code followed by 10-digit mobile number.

## Notifications (Toast System)

All user-initiated actions must provide toast feedback:

-   **Success** — green, 4 second auto-dismiss
-   **Error** — red, 6 second auto-dismiss (longer for reading)
-   **Warning** — amber, 4 second auto-dismiss
-   **Info** — blue, 4 second auto-dismiss

Toast rules:

-   Every CRUD operation (create / update / delete) shows a success toast
-   Every API failure shows an error toast
-   Toasts stack vertically from bottom-right
-   Each toast is dismissible via close button
-   Toasts use slide-in/slide-out animation
-   Screen readers receive `aria-live="polite"` announcements

## Implementation Status

| Feature | Status |
|---|---|
| Currency (₹ INR) | ✅ Implemented |
| en-IN locale formatting | ✅ Implemented |
| Indian numbering (L/Cr) | ✅ Implemented |
| Phone format +91 | ✅ Implemented |
| Toast on CRUD success | ✅ Implemented |
| Toast on API error | ✅ Implemented |
| Mock data (Indian phones) | ✅ Updated |
