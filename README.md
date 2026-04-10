# Visitor Challenge

## What This Challenge Is About

This challenge is about language evolution and architectural extensibility.

The repository implements a small expression language in Java together with many tools built on top of it: printers, evaluators, folders, reducers, metadata collectors, and traversal helpers.

Version 1 of the language already exists and already has a substantial toolset.

Now the language evolves. Version 2 adds one new syntax construct. In this repository that construct is a lambda expression, but lambda is only the example. The real problem is how to extend the language without damaging the design.

## The Design Problem

Once a new construct is added, there are two obvious but bad directions:

1. Make every existing tool handle the new construct.
2. Rebuild the old toolchain again for the new language version.

The first option creates too much coupling and makes it easy to accidentally run a tool on a language version it does not semantically support.

The second option creates duplication, larger releases, weaker reuse, and constant maintenance drift because shared improvements now have to be repeated in two places.

This codebase is structured around a third option: preserve the existing V1 behavior, reuse it where possible, and extend only the pieces that truly need to understand the new construct.

That is the point of the challenge. It is meant to show why the visitor pattern scales better than scattered `instanceof`-style logic when a language grows.

Some handlers are good examples of why this separation matters:

- `cLikeSyntaxPrinter` makes sense for V1, but not for V2 in this repository, because the new construct has no C-like equivalent worth supporting here.
- the V1 internationalization dictionaries already cover many languages. in V2 we have dropped that feature, so we don't need a translated name for the new construct in every existing language.
- a generic dictionary reader can still exist in both versions, but its input type changes with the language version: V1 reads from `IExpressionDict`, while V2 reads from `IExpressionDict2`.

If this were a larger system with hundreds of handlers, forcing all of them to migrate just because one new construct was added would be expensive and error-prone.

## Compatibility Goal

The intended behavior is:

- a V1-only tool should work on V1 expressions
- a V1-only tool should not work on V2 expressions
- a V2-capable tool should work on V1 expressions
- a V2-capable tool should work on V2 expressions

The challenge is to complete the missing code so this separation remains intact while maximizing reuse.

## Task

Fill in the missing production code for the extended language and its handlers.

You are not being asked to redesign the system. You are being asked to complete the extension mechanism already suggested by the codebase.

The tests define the expected behavior and also show how the new version is supposed to fit into the existing architecture.

A good solution should:

- preserve the visitor-based structure
- reuse existing V1 behavior where possible
- add V2-specific behavior only where the new construct requires it
- keep the separation between V1-only and V2-capable tools

In other words, the task is not to make every handler understand the new syntax. The task is to extend only the handlers that should extend, while leaving the others isolated on purpose.

Your goal is to make the tests pass without deleting or editing any existing file. You may only add code.

If the tests pass, the score is based on the number of added tokens matching `\w+`. Lower is better.

## What The Specs Are Checking

The specs are not just checking that the new syntax exists. They are checking that the extended language integrates correctly with the rest of the system, including:

- syntax printing
- traversal helpers
- metadata extraction
- state-based handlers
- constant folding
- substitution and capture avoidance

## Rules

- Do not delete files.
- Do not edit existing files.
- Do not modify or remove the provided tests.
- Solve the task by adding code only.

## Validation

Run the full check with:

```bash
./run_tests.sh
```