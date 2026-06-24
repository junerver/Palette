# CommonMark Extended Coverage

## Emphasis and Strong

*italic* and **bold** and ***bold italic***.

_italic_ and __bold__ and ___bold italic___.

Mixed: **bold *nested italic* bold**.

## Links

Inline [link](https://example.com "title") with title.

Reference [link][ref] and [shortcut].

[ref]: https://example.com/reference "Reference Title"

## Images

![alt](https://example.com/img.png "Image Title")

## Code

Inline `code` and ```multi ` backtick``` code.

```kotlin
fun main() = println("Hello")
```

## Lists

Tight list:
- a
- b
- c

Ordered:
1. first
2. second
3. third

## Blockquotes

> Nested
> > blockquote
> with continuation.

## HTML

Inline <em>emphasis</em> and <strong>strong</strong>.

<script>alert('xss')</script>

## Hard Break

Line with two spaces  
hard break.

Line with backslash\
hard break.
