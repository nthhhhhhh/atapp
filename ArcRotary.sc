/*

ArcRotary fills leds incrementally up to the current position
and decrements respectively, returns led position 0-63.

https://github.com/nthhisst


*/

ArcRotary : ArcEncoder {

    *new { | your_arc, sensitivity_level |
        ^ super.new.initArcRotary(your_arc, sensitivity_level);
    }

    initArcRotary { | your_arc, sensitivity_level |

        arc = your_arc;
        sensitivity = Array.fill(4, sensitivity_level);
        current_led = Array.fill(4, 0);
        gathered_delta = Array.fill(4, 0);

        arc_map = [
            Array.fill(64,0),
            Array.fill(64, 0),
            Array.fill(64, 0),
            Array.fill(64, 0)
        ];

    }

    spin { | knob_n, delta |

        if(delta.isStrictlyPositive)
        {
            if(current_led[knob_n] < 63)
            {
                gathered_delta[knob_n] = gathered_delta[knob_n] + delta;

                while{ (gathered_delta[knob_n] >= sensitivity[knob_n]) && (current_led[knob_n] < 63) }
                {
                    current_led[knob_n] = current_led[knob_n] + 1;
                    arc_map[knob_n][current_led @ knob_n] = current_led[knob_n].linlin(0, 63, 2, 15).asInteger;
                    gathered_delta[knob_n] = gathered_delta[knob_n] - sensitivity[knob_n];
                };

                arc.ringmap(knob_n, arc_map[knob_n]);

                if(current_led[knob_n] >= 63)
                {
                    current_led[knob_n] = 63;

                    arc.ringmap(knob_n, #[
                        15, 2, 2, 2, 2, 3, 3, 3,
                        3, 3, 4, 4, 4, 4, 4, 5,
                        5, 5, 5, 5, 6, 6, 6, 6,
                        6, 7, 7, 7, 7, 7, 8, 8,
                        8, 8, 9, 9, 9, 9, 9, 10,
                        10, 10, 10, 10, 11, 11, 11, 11,
                        11, 12, 12, 12, 12, 12, 13, 13,
                        13, 13, 13, 14, 14, 14, 14, 15 ]);

                    gathered_delta[knob_n] = 0;

                    ^ current_led @ knob_n;
                };

            };
        };

        if(delta.isNegative)
        {
            if(current_led[knob_n] == 0)
            {
                gathered_delta[knob_n] = 0;
                current_led[knob_n] = 0;

                arc.ringmap(knob_n, #[
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0]);

                ^ current_led @ knob_n;
            };


            if(current_led[knob_n] > 0)
            {
                gathered_delta[knob_n] = gathered_delta[knob_n] + delta.abs;

                while{ (gathered_delta[knob_n] >= sensitivity[knob_n]) && (current_led[knob_n] > 0) }
                {
                    arc_map[knob_n][current_led @ knob_n] = 0;
                    current_led[knob_n] = current_led[knob_n] - 1;
                    gathered_delta[knob_n] = gathered_delta[knob_n] - sensitivity[knob_n];
                };

                arc.ringmap(knob_n, arc_map[knob_n]);
            };
        };

        ^current_led @ knob_n;
    }

}