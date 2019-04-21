<?php

function cases_holder_isset() {
    $container = 'default';
    <weak_warning descr="'$container = $value ?? 'default'' can be used instead (reduces cognitive load).">if</weak_warning> (isset($value)) {
        $container = $value;
    }

    /* false-positives: value or container mismatches, assignment by reference */
    $container = 'default';   if (isset($value)) { $container = trim($value); }
    $container = 'default';   if (isset($value)) { $value = $value; }
    $container = &$reference; if (isset($value)) { $container = $value; }

    <weak_warning descr="'$container = $value ?? 'default'' can be used instead (reduces cognitive load).">if</weak_warning> (isset($value)) {
        $container = $value;
    } else {
        $container = 'default';
    }

    if (isset($value)) {
        $container = $value;
    } else <weak_warning descr="'$container = $value ?? 'default'' can be used instead (reduces cognitive load).">if</weak_warning> (isset($value)) {
        $container = $value;
    } else {
        $container = 'default';
    }

    /* false-positives: value or container mismatches */
    if (isset($value)) { $container = trim($value); } else { $container = 'default'; }
    if (isset($value)) { $value = $value; } else { $container = 'default'; }

    <weak_warning descr="'return $value ?? 'default'' can be used instead (reduces cognitive load).">if</weak_warning> (isset($value)) {
        return $value;
    } else {
        return 'default';
    }

    /* false-positives: value mismatches */
    if (isset($value)) { return trim($value); } else { return 'default'; }

    <weak_warning descr="'return $value ?? 'default'' can be used instead (reduces cognitive load).">if</weak_warning> (isset($value)) {
        return $value;
    }
    return 'default';

    /* false-positives: value mismatches, multi-assignments (refactoring changes semantics) */
    if (isset($value)) { return trim($value); } return 'default';
    $one = $two = 'default'; if (isset($value)) { $one = $value; }
}