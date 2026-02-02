# Navigation Bar Redesign Plan

## Overview
Move the user name and authentication button into a top navigation bar using Pico CSS's `<nav>` element.

## Current Structure Analysis

### Existing Layout
```html
<header class="container">
    <hgroup>
        <h1>Basic template</h1>
        <p th:text="${message}">Hello HTMX</p>
    </hgroup>
</header>

<main class="container">
    <section id="auth">
        <div th:if="${authenticated}">
            <p>[[${'Hello ' + displayName}]]</p>
            <a class="contrast" href="/logout">Sign out</a>
        </div>
        <div th:unless="${authenticated}">
            <p>not signed in</p>
            <a class="contrast" href="/oauth2/authorization/google">Sign in with Google</a>
        </div>
    </section>
    <!-- Drive files section -->
</main>
```

## Proposed Navigation Bar Structure

### New Layout with `<nav>` Element

```html
<nav class="container">
    <ul>
        <li><strong>Google Drive Organizer</strong></li>
    </ul>
    <ul>
        <li th:if="${authenticated}">
            <span th:text="${displayName}">User Name</span>
        </li>
        <li>
            <a th:if="${authenticated}" href="/logout" role="button" class="secondary">Sign out</a>
            <a th:unless="${authenticated}" href="/oauth2/authorization/google" role="button">Sign in with Google</a>
        </li>
    </ul>
</nav>
```

## Pico CSS Navigation Pattern

Pico CSS uses a specific pattern for navigation bars:
- `<nav>` element with `container` class for proper spacing
- Two `<ul>` elements:
  - First `<ul>`: Left-aligned items (brand/logo)
  - Second `<ul>`: Right-aligned items (navigation links, buttons)
- `<li>` elements contain the actual content
- Use `role="button"` on links to style them as buttons

## Implementation Steps

### Step 1: Create Navigation Bar
- Replace current header with `<nav>` element
- Add app name on the left side
- Add authentication controls on the right side

### Step 2: Update Authentication Display
- Show user's display name when authenticated
- Show appropriate button (Sign in vs Sign out)
- Use Pico CSS button styling with `role="button"`

### Step 3: Simplify Main Content
- Remove the separate `#auth` section from main
- Keep only the Google Drive files section
- Optionally move the message display to a better location

### Step 4: Optional Enhancements
- Consider adding the subtitle/message below the nav bar
- Use `<header>` for additional page context if needed
- Ensure proper spacing and visual hierarchy

## Visual Layout

```
┌─────────────────────────────────────────────────────────┐
│ [Google Drive Organizer]          [User Name] [Sign out]│
└─────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────┐
│                                                           │
│  Main Content Area                                        │
│  - Google Drive Files List                                │
│                                                           │
└─────────────────────────────────────────────────────────┘
```

## Benefits

1. **Better UX**: Authentication controls always visible at the top
2. **Cleaner Layout**: Follows standard web app patterns
3. **Pico CSS Native**: Uses built-in navigation styling
4. **Responsive**: Pico CSS nav elements are mobile-friendly
5. **Semantic HTML**: Proper use of `<nav>` element

## Files to Modify

- [`src/main/resources/templates/index.html`](../src/main/resources/templates/index.html) - Update HTML structure

## No Backend Changes Required

The Thymeleaf template variables remain the same:
- `${authenticated}` - boolean flag
- `${displayName}` - user's display name
- `${message}` - optional message
- `${driveFiles}` - list of drive files
