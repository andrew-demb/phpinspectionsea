<?php

    /* !... equivalent cases */
    $x = <weak_warning descr="[EA] Can be replaced with '! $x5'.">!!!!!$x5</weak_warning>;
    $x = <weak_warning descr="[EA] Can be replaced with '! $x3'.">!!!$x3</weak_warning>;
    $x = <weak_warning descr="[EA] Can be replaced with '! ($x7 || $x7)'.">!!! ($x7 || $x7)</weak_warning>;


    /* (bool)... equivalent cases */
    $x = <weak_warning descr="[EA] Can be replaced with '(bool) $x4'.">!!!!$x4</weak_warning>;
    $x = <weak_warning descr="[EA] Can be replaced with '(bool) $x2'.">!!$x2</weak_warning>;
    $x = <weak_warning descr="[EA] Can be replaced with '(bool) ($x6 || $x6)'.">!! ($x6 || $x6)</weak_warning>;

    /* parentheses handling */
    $x = <weak_warning descr="[EA] Can be replaced with '(bool) $x2'.">!(!(($x2)))</weak_warning>;
    $x = <weak_warning descr="[EA] Can be replaced with '! $x2'.">!(!((!$x2)))</weak_warning>;

    /* false-positives */
    $x = !$x1;