<?php

function cases_holder_isset() {
    $container = $value ?? 'default';

    /* false-positives: value or container mismatches, assignment by reference */
    $container = 'default';   if (isset($value)) { $container = trim($value); }
    $container = 'default';   if (isset($value)) { $value = $value; }
    $container = &$reference; if (isset($value)) { $container = $value; }

    $container = $value ?? 'default';

    if (isset($value)) {
        $container = $value;
    } else {
        $container = $value ?? 'default';
    }

    /* false-positives: value or container mismatches */
    if (isset($value)) { $container = trim($value); } else { $container = 'default'; }
    if (isset($value)) { $value = $value; } else { $container = 'default'; }

    return $value ?? 'default';

    /* false-positives: value mismatches */
    if (isset($value)) { return trim($value); } else { return 'default'; }

    return $value ?? 'default';

    /* false-positives: value mismatches, multi-assignments (refactoring changes semantics) */
    if (isset($value)) { return trim($value); } return 'default';
    $one = $two = 'default'; if (isset($value)) { $one = $value; }
}