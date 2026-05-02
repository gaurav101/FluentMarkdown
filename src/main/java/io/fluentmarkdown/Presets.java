package io.fluentmarkdown;

/**
 * Factory methods for built-in CSS-framework presets.
 *
 * <p>Each method returns a fully configured, immutable {@link StyleConfig}.
 * Callers that need to extend or override a preset can use
 * {@link StyleConfig.Builder#mergeFrom(StyleConfig)}:
 *
 * <pre>{@code
 * StyleConfig custom = StyleConfig.builder()
 *     .mergeFrom(Presets.tailwind())
 *     .tag("p", "mb-6 text-gray-700 leading-loose")   // overrides the preset
 *     .build();
 * }</pre>
 *
 * All class strings follow the conventions of the respective framework and are
 * intentionally opinionated — treat them as sensible defaults, not mandates.
 */
public final class Presets {

    private Presets() { /* utility class — no instances */ }

    // ── Tailwind CSS ───────────────────────────────────────────────────────────

    /**
     * Returns a {@link StyleConfig} that maps common Markdown-generated tags to
     * Tailwind CSS utility classes.
     *
     * <p>Designed for Tailwind v3+. If you use the Tailwind Typography plugin
     * ({@code @tailwindcss/typography}) you may prefer to wrap the output in a
     * {@code <div class="prose">} element instead.
     *
     * @return immutable Tailwind preset
     */
    public static StyleConfig tailwind() {
        return StyleConfig.builder()
            // Headings
            .tag("h1", "text-4xl font-extrabold tracking-tight mb-4 mt-6")
            .tag("h2", "text-3xl font-bold tracking-tight mb-3 mt-5")
            .tag("h3", "text-2xl font-semibold mb-2 mt-4")
            .tag("h4", "text-xl font-semibold mb-2 mt-3")
            .tag("h5", "text-lg font-medium mb-1 mt-2")
            .tag("h6", "text-base font-medium mb-1 mt-2")
            // Body text
            .tag("p",  "mb-4 leading-relaxed text-base")
            // Emphasis & strong
            .tag("em",     "italic")
            .tag("strong", "font-bold")
            // Code
            .tag("code", "font-mono text-sm bg-gray-100 rounded px-1 py-0.5")
            .tag("pre",  "bg-gray-900 text-gray-100 rounded-lg p-4 overflow-x-auto mb-4 text-sm font-mono")
            // Quotes
            .tag("blockquote", "border-l-4 border-gray-300 pl-4 italic text-gray-600 my-4")
            // Lists
            .tag("ul", "list-disc list-inside mb-4 space-y-1")
            .tag("ol", "list-decimal list-inside mb-4 space-y-1")
            .tag("li", "leading-relaxed")
            // Links
            .tag("a", "text-blue-600 hover:text-blue-800 underline underline-offset-2")
            // Table
            .tag("table", "w-full border-collapse text-sm mb-4")
            .tag("thead", "bg-gray-100")
            .tag("th", "border border-gray-300 px-3 py-2 text-left font-semibold")
            .tag("td", "border border-gray-300 px-3 py-2")
            // Misc
            .tag("hr",  "my-6 border-t border-gray-300")
            .tag("img", "max-w-full h-auto rounded")
            .build();
    }

    // ── Bootstrap ─────────────────────────────────────────────────────────────

    /**
     * Returns a {@link StyleConfig} that maps common Markdown-generated tags to
     * Bootstrap 5 utility / component classes.
     *
     * @return immutable Bootstrap preset
     */
    public static StyleConfig bootstrap() {
        return StyleConfig.builder()
            // Headings — Bootstrap display helpers give more visual weight
            .tag("h1", "display-4 fw-bold mb-3")
            .tag("h2", "display-5 fw-semibold mb-3")
            .tag("h3", "h3 fw-semibold mb-2")
            .tag("h4", "h4 fw-semibold mb-2")
            .tag("h5", "h5 fw-semibold mb-1")
            .tag("h6", "h6 fw-semibold mb-1")
            // Body text
            .tag("p", "mb-3")
            // Emphasis & strong
            .tag("em",     "fst-italic")
            .tag("strong", "fw-bold")
            // Code
            .tag("code", "font-monospace bg-light rounded px-1")
            .tag("pre",  "bg-dark text-light rounded p-3 overflow-auto mb-3")
            // Quotes — Bootstrap's blockquote component
            .tag("blockquote", "blockquote border-start border-4 ps-3 text-muted my-3")
            // Lists — use Bootstrap's list-group look via utilities
            .tag("ul", "mb-3 ps-4")
            .tag("ol", "mb-3 ps-4")
            .tag("li", "mb-1")
            // Links
            .tag("a", "link-primary")
            // Table — Bootstrap table component
            .tag("table", "table table-bordered table-hover mb-4")
            .tag("thead",  "table-light")
            .tag("th", "fw-semibold")
            // Misc
            .tag("hr",  "my-4")
            .tag("img", "img-fluid rounded")
            .build();
    }

    // ── Bulma ─────────────────────────────────────────────────────────────────

    /**
     * Returns a {@link StyleConfig} for <a href="https://bulma.io">Bulma</a> CSS.
     *
     * <p>Bulma relies heavily on modifier classes; this preset covers the most
     * common Markdown output elements.
     *
     * @return immutable Bulma preset
     */
    public static StyleConfig bulma() {
        return StyleConfig.builder()
            .tag("h1", "title is-1")
            .tag("h2", "title is-2")
            .tag("h3", "title is-3")
            .tag("h4", "title is-4")
            .tag("h5", "title is-5")
            .tag("h6", "title is-6")
            .tag("p",  "block")
            .tag("em",     "is-italic")
            .tag("strong", "has-text-weight-bold")
            .tag("code",   "tag is-warning is-light")
            .tag("pre",    "box has-background-dark has-text-light")
            .tag("blockquote", "block is-italic has-text-grey pl-4")
            .tag("ul", "block")
            .tag("ol", "block")
            .tag("a",  "has-text-link")
            .tag("table", "table is-bordered is-striped is-hoverable is-fullwidth")
            .tag("hr",  "block")
            .tag("img", "image")
            .build();
    }
}
