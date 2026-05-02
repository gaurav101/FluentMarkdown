# FluentMarkdown

A lightweight, zero-boilerplate Java library that converts Markdown to **styled HTML** for any CSS framework — Tailwind, Bootstrap, Bulma, or your own custom classes — using a clean fluent API built on top of [commonmark-java](https://github.com/commonmark/commonmark-java).

```java
String html = Markdown.from(text).withTailwind().toHtml();
```

---

## Table of Contents

- [Requirements](#requirements)
- [Installation](#installation)
- [Quick Start](#quick-start)
- [Step-by-Step Guide](#step-by-step-guide)
  - [Step 1 — Parse plain Markdown](#step-1--parse-plain-markdown)
  - [Step 2 — Apply a built-in preset](#step-2--apply-a-built-in-preset)
  - [Step 3 — Use a custom style config](#step-3--use-a-custom-style-config)
  - [Step 4 — Extend or override a preset](#step-4--extend-or-override-a-preset)
  - [Step 5 — Enable safe (XSS-sanitised) output](#step-5--enable-safe-xss-sanitised-output)
- [Built-in Presets](#built-in-presets)
  - [Tailwind CSS](#tailwind-css)
  - [Bootstrap 5](#bootstrap-5)
  - [Bulma](#bulma)
- [StyleConfig Reference](#styleconfig-reference)
- [API Reference](#api-reference)
- [Project Structure](#project-structure)
- [Building from Source](#building-from-source)
- [Running the Tests](#running-the-tests)
- [FAQ](#faq)

---

## Requirements

| Requirement | Version |
|-------------|---------|
| Java | 17 or later |
| Maven | 3.8 or later |
| commonmark-java | 0.22.0 (pulled in automatically) |

---

## Installation

### Option A — Add the JAR via Maven (local install)

If you haven't published the library to a remote repository, first install it into your local Maven cache:

```bash
cd FluentMarkdown
mvn install
```

Then add this dependency to your project's `pom.xml`:

```xml
<dependency>
    <groupId>io.fluentmarkdown</groupId>
    <artifactId>fluentmarkdown</artifactId>
    <version>1.0.0</version>
</dependency>
```

### Option B — Copy the source into your project

The library is only four files. You can copy the `io/fluentmarkdown` package directly into your `src/main/java` directory and add `commonmark` to your own `pom.xml`:

```xml
<dependency>
    <groupId>org.commonmark</groupId>
    <artifactId>commonmark</artifactId>
    <version>0.22.0</version>
</dependency>
```

---

## Quick Start

```java
import io.fluentmarkdown.Markdown;

public class Example {
    public static void main(String[] args) {
        String markdown = """
                # Hello, FluentMarkdown!

                This is a **bold** statement with a [link](https://example.com).

                > A styled blockquote.

                - Item one
                - Item two
                """;

        // Tailwind CSS output
        String tailwindHtml = Markdown.from(markdown).withTailwind().toHtml();

        // Bootstrap 5 output
        String bootstrapHtml = Markdown.from(markdown).withBootstrap().toHtml();

        // Plain HTML (no CSS classes)
        String plainHtml = Markdown.from(markdown).toHtml();

        System.out.println(tailwindHtml);
    }
}
```

---

## Step-by-Step Guide

### Step 1 — Parse plain Markdown

The simplest use-case: convert Markdown to HTML with no CSS classes attached.

```java
String html = Markdown.from("# Hello\n\nSome paragraph.").toHtml();
```

**Output:**

```html
<h1>Hello</h1>
<p>Some paragraph.</p>
```

---

### Step 2 — Apply a built-in preset

Call one of the three preset methods before `.toHtml()` to inject CSS framework classes automatically.

```java
// Tailwind CSS
String html = Markdown.from(markdown).withTailwind().toHtml();

// Bootstrap 5
String html = Markdown.from(markdown).withBootstrap().toHtml();

// Bulma
String html = Markdown.from(markdown).withBulma().toHtml();
```

**Example — Tailwind output for `# Hello`:**

```html
<h1 class="text-4xl font-extrabold tracking-tight mb-4 mt-6">Hello</h1>
```

**Example — Bootstrap output for `> A quote`:**

```html
<blockquote class="blockquote border-start border-4 ps-3 text-muted my-3">
  <p class="mb-3">A quote</p>
</blockquote>
```

---

### Step 3 — Use a custom style config

Create a `StyleConfig` with `StyleConfig.builder()` and map any HTML tag name to a string of CSS classes.

```java
import io.fluentmarkdown.Markdown;
import io.fluentmarkdown.StyleConfig;

StyleConfig myConfig = StyleConfig.builder()
        .tag("h1", "page-title")
        .tag("h2", "section-title")
        .tag("p",  "body-text")
        .tag("a",  "link link--primary")
        .tag("code", "inline-code")
        .build();

String html = Markdown.from(markdown).withStyle(myConfig).toHtml();
```

Tags that are **not** listed in your config are rendered without a `class` attribute — there are no silent defaults.

---

### Step 4 — Extend or override a preset

Use `mergeFrom()` to start from a preset and then change specific tags:

```java
import io.fluentmarkdown.Markdown;
import io.fluentmarkdown.Presets;
import io.fluentmarkdown.StyleConfig;

StyleConfig custom = StyleConfig.builder()
        .mergeFrom(Presets.tailwind())          // start from the Tailwind preset
        .tag("p",  "mb-6 text-gray-700 text-lg leading-loose")  // override paragraph
        .tag("a",  "text-indigo-600 underline") // override link
        .tag("table", "w-full text-sm")         // override table
        .build();

String html = Markdown.from(markdown).withStyle(custom).toHtml();
```

`mergeFrom()` copies all tag mappings from the preset into the builder first; subsequent `.tag()` calls overwrite only the tags you name, leaving the rest intact.

---

### Step 5 — Enable safe (XSS-sanitised) output

When the Markdown source comes from **untrusted user input**, chain `.safe()` before `.toHtml()`. This strips raw HTML blocks and inline HTML from the source before rendering.

```java
String userInput = "<script>alert('xss')</script>\n\n# Safe content";

String safeHtml = Markdown.from(userInput)
        .withTailwind()
        .safe()          // strips raw HTML
        .toHtml();

// The <script> tag is gone; normal Markdown is still rendered.
```

> **Recommendation:** Always call `.safe()` for user-generated content, form submissions, or any text that didn't originate from your own application.

---

## Built-in Presets

### Tailwind CSS

Applied by `.withTailwind()`. Targets Tailwind v3+. Each tag and its default classes:

| Tag | Classes |
|-----|---------|
| `h1` | `text-4xl font-extrabold tracking-tight mb-4 mt-6` |
| `h2` | `text-3xl font-bold tracking-tight mb-3 mt-5` |
| `h3` | `text-2xl font-semibold mb-2 mt-4` |
| `h4` | `text-xl font-semibold mb-2 mt-3` |
| `p` | `mb-4 leading-relaxed text-base` |
| `a` | `text-blue-600 hover:text-blue-800 underline underline-offset-2` |
| `code` | `font-mono text-sm bg-gray-100 rounded px-1 py-0.5` |
| `pre` | `bg-gray-900 text-gray-100 rounded-lg p-4 overflow-x-auto mb-4 text-sm font-mono` |
| `blockquote` | `border-l-4 border-gray-300 pl-4 italic text-gray-600 my-4` |
| `ul` | `list-disc list-inside mb-4 space-y-1` |
| `ol` | `list-decimal list-inside mb-4 space-y-1` |
| `table` | `w-full border-collapse text-sm mb-4` |
| `th` | `border border-gray-300 px-3 py-2 text-left font-semibold` |
| `td` | `border border-gray-300 px-3 py-2` |
| `hr` | `my-6 border-t border-gray-300` |
| `img` | `max-w-full h-auto rounded` |

### Bootstrap 5

Applied by `.withBootstrap()`.

| Tag | Classes |
|-----|---------|
| `h1` | `display-4 fw-bold mb-3` |
| `h2` | `display-5 fw-semibold mb-3` |
| `p` | `mb-3` |
| `blockquote` | `blockquote border-start border-4 ps-3 text-muted my-3` |
| `code` | `font-monospace bg-light rounded px-1` |
| `pre` | `bg-dark text-light rounded p-3 overflow-auto mb-3` |
| `table` | `table table-bordered table-hover mb-4` |
| `a` | `link-primary` |
| `img` | `img-fluid rounded` |

### Bulma

Applied by `.withBulma()`.

| Tag | Classes |
|-----|---------|
| `h1` | `title is-1` |
| `h2` | `title is-2` |
| `p` | `block` |
| `code` | `tag is-warning is-light` |
| `pre` | `box has-background-dark has-text-light` |
| `table` | `table is-bordered is-striped is-hoverable is-fullwidth` |
| `a` | `has-text-link` |

---

## StyleConfig Reference

`StyleConfig` is an **immutable value object**. Build one with `StyleConfig.builder()`:

```java
StyleConfig config = StyleConfig.builder()
    .tag("tagName", "class-a class-b")   // add or overwrite a mapping
    .mergeFrom(anotherConfig)            // bulk-import mappings from another config
    .build();                            // returns the immutable instance
```

| Method | Description |
|--------|-------------|
| `tag(String tag, String classes)` | Maps an HTML tag name (case-insensitive) to a space-separated class string |
| `mergeFrom(StyleConfig other)` | Copies all mappings from `other`; duplicate keys are overwritten |
| `build()` | Returns the immutable `StyleConfig` |
| `classesFor(String tag)` | (on the built instance) Returns the class string, or `null` if unmapped |
| `asMap()` | Returns an unmodifiable `Map<String, String>` of all mappings |

---

## API Reference

### `Markdown` (entry class)

| Method | Returns | Description |
|--------|---------|-------------|
| `Markdown.from(String text)` | `Markdown` | Creates a new builder for the given Markdown source |
| `.withTailwind()` | `Markdown` | Applies the Tailwind CSS preset |
| `.withBootstrap()` | `Markdown` | Applies the Bootstrap 5 preset |
| `.withBulma()` | `Markdown` | Applies the Bulma preset |
| `.withStyle(StyleConfig config)` | `Markdown` | Applies a fully custom `StyleConfig` |
| `.safe()` | `Markdown` | Enables HTML sanitisation (strips raw HTML from source) |
| `.toHtml()` | `String` | Parses and renders the Markdown; repeatable — same result every call |

### `StyleAttributeProvider`

You don't normally use this class directly, but it's available if you need to integrate with a manually constructed `HtmlRenderer`:

```java
HtmlRenderer renderer = HtmlRenderer.builder()
        .attributeProviderFactory(StyleAttributeProvider.factory(myConfig))
        .build();
```

---

## Project Structure

```
FluentMarkdown/
├── pom.xml
└── src/
    ├── main/java/io/fluentmarkdown/
    │   ├── Markdown.java               ← fluent builder & entry point
    │   ├── StyleConfig.java            ← immutable tag → class map + builder
    │   ├── StyleAttributeProvider.java ← commonmark AttributeProvider impl
    │   └── Presets.java                ← Tailwind / Bootstrap / Bulma factories
    └── test/java/io/fluentmarkdown/
        └── MarkdownTest.java           ← unit tests
```

---

## Building from Source

```bash
# Clone or download the project, then:
cd FluentMarkdown

# Compile, run tests, and package the JAR
mvn install

# Skip tests if you just want the JAR quickly
mvn install -DskipTests

# Clean before building (recommended after pulling changes)
mvn clean install
```

The JAR is produced at `target/fluentmarkdown-1.0.0.jar` and installed to your local `~/.m2` repository.

---

## Running the Tests

```bash
mvn test
```

The test suite covers:

- Plain (unstyled) rendering
- All three built-in presets (Tailwind, Bootstrap, Bulma)
- Custom `StyleConfig` injection
- Preset extension via `mergeFrom()`
- Safe mode (XSS stripping)
- Edge cases: null input, empty string, repeated `toHtml()` calls

---

## FAQ

**Can I use this with Gradle?**

Yes. Install the JAR locally with `mvn install`, then add the dependency:

```groovy
// build.gradle
dependencies {
    implementation 'io.fluentmarkdown:fluentmarkdown:1.0.0'
}
```

**Can I apply two presets at the same time?**

Not directly — `.withStyle()` replaces the previous config. To combine them, merge manually:

```java
StyleConfig combined = StyleConfig.builder()
        .mergeFrom(Presets.tailwind())
        .mergeFrom(Presets.bootstrap())   // bootstrap entries overwrite tailwind ones
        .build();
```

**Does this support Markdown extensions (tables, strikethrough, etc.)?**

Tables are supported natively by commonmark-java and styled by the presets. For other extensions (e.g., `commonmark-ext-gfm-strikethrough`), add the extension jar to your pom and wire it through the commonmark `Parser` inside `Markdown.toHtml()`.

**Is the `Markdown` builder thread-safe?**

Each `Markdown.from(...)` call creates a fresh, independent builder. Do not share a builder across threads. The `StyleConfig` instances (including presets) are fully thread-safe — share those freely.

**Will the library modify classes added by other AttributeProviders?**

No. `StyleAttributeProvider` uses `Map.merge()` which appends to any existing `class` attribute rather than overwriting it, so it is safe to stack multiple providers.
