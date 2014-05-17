/*

ArcEncoder Rotary Traktor Style

https://github.com/nthhisst

*/

ArcRotaryTraktor : ArcRotary {


    *new { | your_arc, sensitivity_level |

        ^ super.new(your_arc, sensitivity_level).initArcRotaryTraktor;

    }

    initArcRotaryTraktor {

        for(0, 3,{ arg knob_n;
            arc_map[knob_n] = [
                2, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0,
                0, 0, 0, 0, 0, 0, 0, 0 ];
        });

        for(0, 3, { arg knob_n;
            current_led[knob_n] = 0;
            arc.ringmap(knob_n, arc_map[knob_n]);
        });

    }

    spin { | knob_n, delta |

        // ---------------------------------------- 0 -> 32

        if ((current_led[knob_n] <= 32) and:
            (current_led[knob_n].isStrictlyPositive) and:
            (delta.isStrictlyPositive))
        {
            gathered_delta[knob_n] = gathered_delta[knob_n] + delta;

            while { (gathered_delta[knob_n] >= sensitivity[knob_n]) and: (current_led[knob_n] < 33) }
            {
                // sets the current_led value
                arc_map[knob_n][current_led @ knob_n] = current_led[knob_n].linlin(0, 32, 2, 15).asInteger;

                // moves current position up by 1
                current_led[knob_n] = current_led[knob_n] + 1;

                // collect gathered delta
                gathered_delta[knob_n] = gathered_delta[knob_n] - sensitivity[knob_n];
            };

            arc.ringmap(knob_n, arc_map[knob_n]);

            if ( current_led[knob_n] >= 32 )
            {
                current_led[knob_n] = 32;

                gathered_delta[knob_n] = 0;

                arc_map[knob_n] = [
                    2, 2, 2, 3, 3, 4, 4, 4,
                    5, 5, 6, 6, 6, 7, 7, 8,
                    8, 8, 9, 9, 10, 10, 10, 11,
                    11, 12, 12, 12, 13, 13, 14, 14,
                    15, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0];

                ^ current_led[knob_n].linlin(-32, 32, 0, 64);
            };
        };

        if ((current_led[knob_n] <= 32) and:
            (current_led[knob_n].isStrictlyPositive) and:
            (delta.isNegative))
        {
            gathered_delta[knob_n] = gathered_delta[knob_n] + delta.abs;

            while { gathered_delta[knob_n] >= sensitivity[knob_n] and: current_led[knob_n].isStrictlyPositive }
            {
                arc_map[knob_n][current_led @ knob_n] = 0;
                current_led[knob_n] = current_led[knob_n] - 1;
                gathered_delta[knob_n] = gathered_delta[knob_n] - sensitivity[knob_n];
            };


            arc.ringmap(knob_n, arc_map[knob_n]);
        };

        // ---------------------------------------- 0 -> 32


        // ---------------------------------------- 0 -> -32

        if ((current_led[knob_n] <= 0) and:
            (current_led[knob_n] >= -32) and:
            (delta.isNegative))
        {
            gathered_delta[knob_n] = gathered_delta[knob_n] + delta.abs;

            while { (gathered_delta[knob_n] >= sensitivity[knob_n]) and: (current_led[knob_n] > -31) }
            {
                // adjust negative led position as arc protocol is 0-63
                arc_map[knob_n][(current_led[knob_n]).abs.linlin(0, 32, 63, 32).asInteger]
                = current_led[knob_n].abs.linlin(0, 32, 2, 15).asInteger;
                current_led[knob_n] = current_led[knob_n] - 1;
                gathered_delta[knob_n] = gathered_delta[knob_n] - sensitivity[knob_n];
            };

            arc.ringmap(knob_n, arc_map[knob_n]);

            if( current_led[knob_n] <= -31 )
            {
                current_led[knob_n] = -32;
                gathered_delta[knob_n] = 0;

                arc_map[knob_n] = [
                    2, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    15, 14, 13, 13, 12, 12, 12, 11,
                    11, 10, 10, 10, 9, 9, 8, 8,
                    8, 7, 7, 6, 6, 6, 5, 5,
                    4, 4, 4, 3, 3, 2, 2, 2 ];

                ^ current_led[knob_n].linlin(-32, 32, 0, 64);
            };
        };

        if ((current_led[knob_n] <= 0) and:
            (current_led[knob_n] >= -32) and:
            (delta.isPositive))
        {
            gathered_delta[knob_n] = gathered_delta[knob_n] + delta.abs;

            while { gathered_delta[knob_n] >= sensitivity[knob_n] }
            {
                if (current_led[knob_n] <= 0)
                {
                    arc_map[knob_n][(current_led[knob_n]).abs.linlin(0, 32, 63, 32).asInteger] = 0;
                    current_led[knob_n] = current_led[knob_n] + 1;
                    gathered_delta[knob_n] = gathered_delta[knob_n] - sensitivity[knob_n];
                } {
                    // sets the current_led value
                    arc_map[knob_n][current_led @ knob_n] = current_led[knob_n].linlin(0, 32, 2, 15).asInteger;

                    // moves current position up by 1
                    current_led[knob_n] = current_led[knob_n] + 1;

                    // collect gathered delta
                    gathered_delta[knob_n] = gathered_delta[knob_n] - sensitivity[knob_n];
                }
            };

            arc.ringmap(knob_n, arc_map[knob_n]);
        };

        // ---------------------------------------- 0 -> -32

        ^ current_led[knob_n].linlin(-32, 32, 0, 64);
    }
}