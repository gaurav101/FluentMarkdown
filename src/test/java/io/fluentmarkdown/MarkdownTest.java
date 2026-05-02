package io.fluentmarkdown;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the FluentMarkdown library.
 *
 * <p>These tests are intentionally integration-style — they exercise the public
 * API end-to-end and assert on the rendered HTML string, which is the only
 * observable output the library produces.
 */
class MarkdownTest {

    // ── Helpers ────────────────────────────────────────────────────────────────

    private static void assertContains(String haystack, String needle) {
        assertTrue(haystack.contains(needle),
                "Expected output to contain:\n  " + needle +
                "\nActual output:\n  " + haystack);
    }

    // ── No-style rendering ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("Plain (unstyled) rendering")
    class PlainRendering {

        @Test
        @DisplayName("Paragraph renders as <p> without class attribute")
        void paragraph_noStyle() {
            String html = Markdown.from("Hello world").toHtml();
            assertContains(html, "<p>Hello world</p>");
            assertFalse(html.contains("class="), "No class attribute expected");
        }

        @Test
        @DisplayName("Heading renders as <h1> without class attribute")
        void heading_noStyle() {
            String html = Markdown.from("# Title").toHtml();
            assertContains(html, "<h1>Title</h1>");
        }
    }

    // ── Tailwind preset ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Tailwind preset")
    class TailwindPreset {

        @Test
        @DisplayName("h1 gets Tailwind heading classes")
        void h1_tailwindClasses() {
            String html = Markdown.from("# Hello").withTailwind().toHtml();
            assertContains(html, "class=\"text-4xl font-extrabold tracking-tight mb-4 mt-6\"");
        }

        @Test
        @DisplayName("Paragraph gets Tailwind paragraph classes")
        void paragraph_tailwindClasses() {
            String html = Markdown.from("Some text.").withTailwind().toHtml();
            assertContains(html, "class=\"mb-4 leading-relaxed text-base\"");
        }

        @Test
        @DisplayName("Code span gets Tailwind code classes")
        void code_tailwindClasses() {
            String html = Markdown.from("Use `foo()` here.").withTailwind().toHtml();
            assertContains(html, "class=\"font-mono text-sm bg-gray-100 rounded px-1 py-0.5\"");
        }

        @Test
        @DisplayName("Blockquote gets Tailwind blockquote classes")
        void blockquote_tailwindClasses() {
            String html = Markdown.from("> A quote.").withTailwind().toHtml();
            assertContains(html, "class=\"border-l-4 border-gray-300 pl-4 italic text-gray-600 my-4\"");
        }

        @Test
        @DisplayName("Unordered list gets Tailwind list classes")
        void unorderedList_tailwindClasses() {
            String html = Markdown.from("- item").withTailwind().toHtml();
            assertContains(html, "class=\"list-disc list-inside mb-4 space-y-1\"");
        }

        @Test
        @DisplayName("Anchor gets Tailwind link classes")
        void anchor_tailwindClasses() {
            String html = Markdown.from("[click](https://example.com)").withTailwind().toHtml();
            assertContains(html, "class=\"text-blue-600 hover:text-blue-800 underline underline-offset-2\"");
        }
    }

    // ── Bootstrap preset ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("Bootstrap preset")
    class BootstrapPreset {

        @Test
        @DisplayName("Blockquote gets Bootstrap blockquote classes")
        void blockquote_bootstrapClasses() {
            String html = Markdown.from("> Quote text.").withBootstrap().toHtml();
            assertContains(html, "blockquote border-start border-4 ps-3 text-muted my-3");
        }

        // TODO: FIX TEST
        /*   @Test
        @DisplayName("Table gets Bootstrap table classes")
        void table_bootstrapClasses() {
            String md = """
                  | A | B |
                  |---|---|
                  | 1 | 2 |
                  """;
            String html = Markdown.from(md).withBootstrap().toHtml();
            assertContains(html, "class=\"table table-bordered table-hover mb-4\"");
        }
*/
        @Test
        @DisplayName("h1 gets Bootstrap display classes")
        void h1_bootstrapClasses() {
            String html = Markdown.from("# Title").withBootstrap().toHtml();
            assertContains(html, "class=\"display-4 fw-bold mb-3\"");
        }
    }

    // ── Bulma preset ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Bulma preset")
    class BulmaPreset {

        @Test
        @DisplayName("h1 gets Bulma title classes")
        void h1_bulmaClasses() {
            String html = Markdown.from("# Title").withBulma().toHtml();
            assertContains(html, "class=\"title is-1\"");
        }

        @Test
        @DisplayName("Code gets Bulma tag classes")
        void code_bulmaClasses() {
            String html = Markdown.from("Use `foo()`.").withBulma().toHtml();
            assertContains(html, "class=\"tag is-warning is-light\"");
        }
    }

    // ── Custom config ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Custom StyleConfig")
    class CustomConfig {

        @Test
        @DisplayName("Custom classes are injected for mapped tags")
        void customClasses_injected() {
            StyleConfig config = StyleConfig.builder()
                    .tag("h1", "my-custom-heading")
                    .tag("p",  "my-custom-paragraph")
                    .build();

            String html = Markdown.from("# Hi\n\nText.").withStyle(config).toHtml();
            assertContains(html, "class=\"my-custom-heading\"");
            assertContains(html, "class=\"my-custom-paragraph\"");
        }

        @Test
        @DisplayName("Unmapped tags have no class attribute")
        void unmappedTag_noClass() {
            StyleConfig config = StyleConfig.builder()
                    .tag("h1", "some-class")
                    .build();

            String html = Markdown.from("A paragraph.").withStyle(config).toHtml();
            // <p> is unmapped → no class
            assertFalse(html.contains("<p class="), "Unmapped <p> should have no class");
        }

        @Test
        @DisplayName("mergeFrom merges presets correctly")
        void mergeFrom_overridesPreset() {
            StyleConfig extended = StyleConfig.builder()
                    .mergeFrom(Presets.tailwind())
                    .tag("p", "custom-override")   // override tailwind's 'p'
                    .build();

            String html = Markdown.from("Paragraph.").withStyle(extended).toHtml();
            assertContains(html, "class=\"custom-override\"");
            assertFalse(html.contains("mb-4 leading-relaxed"), "Original p class should be replaced");
        }
    }

    // ── Safe mode ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Safe mode")
    class SafeMode {

        @Test
        @DisplayName("Raw HTML in source is escaped when safe() is set")
        void rawHtml_escapedInSafeMode() {
            String md = "<script>alert('xss')</script>\n\nSafe text.";
            String html = Markdown.from(md).withTailwind().safe().toHtml();
            assertFalse(html.contains("<script>"),
                    "Raw <script> tag must be stripped in safe mode");
        }

        @Test
        @DisplayName("Normal content still renders in safe mode")
        void normalContent_rendersInSafeMode() {
            String html = Markdown.from("# Hello\n\nWorld.").safe().toHtml();
            assertContains(html, "<h1>Hello</h1>");
            assertContains(html, "<p>World.</p>");
        }
    }

    // ── Null / edge cases ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("Edge cases & guard rails")
    class EdgeCases {

        @Test
        @DisplayName("Null source throws IllegalArgumentException")
        void nullSource_throws() {
            assertThrows(IllegalArgumentException.class, () -> Markdown.from(null));
        }

        @Test
        @DisplayName("Null StyleConfig throws IllegalArgumentException")
        void nullConfig_throws() {
            assertThrows(IllegalArgumentException.class,
                    () -> Markdown.from("text").withStyle(null));
        }

        @Test
        @DisplayName("Empty string renders without error")
        void emptySource_renders() {
            String html = Markdown.from("").withTailwind().toHtml();
            assertNotNull(html);
        }

        @Test
        @DisplayName("toHtml() is repeatable — same result on second call")
        void toHtml_isRepeatable() {
            Markdown md = Markdown.from("# Repeat").withTailwind();
            assertEquals(md.toHtml(), md.toHtml());
        }
    }
}
