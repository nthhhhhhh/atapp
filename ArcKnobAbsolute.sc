/*

ArcKnobAbsolute fills leds incrementally up to the current position
(think of a stack of lit leds) and decrements respectively, returns led position 0-63.

https://github.com/nthhisst


*/

ArcKnobAbsolute{

    var my_arc,
    <>sensitvity,
    my_gathered_delta,
    my_lead_led_position,
    my_arc_map;

    *new { | arc, sensitvity_level |

        ^super.new.initArcKnob(arc, sensitvity_level);
    }

    initArcKnob { | arc, sensitvity_level |

        my_arc = arc;
        sensitvity = sensitvity_level;
        my_arc_map = Array.fill(64, 0);

        my_gathered_delta = 0;
        my_lead_led_position = 0;
    }

    spin { | knob_n, delta |

        if(delta.isStrictlyPositive)
        {
            if(my_lead_led_position < 63)
            {
                my_gathered_delta = my_gathered_delta + delta;

                while{(my_gathered_delta >= sensitvity) && (my_lead_led_position < 63)}
                {
                    my_lead_led_position = my_lead_led_position + 1;
                    my_arc_map[my_lead_led_position] = 15;
                    my_gathered_delta = my_gathered_delta - sensitvity;

                };

                my_arc.ringmap(knob_n, my_arc_map);

                if(my_lead_led_position >= 63)
                {
                    my_lead_led_position = 63;

                    // constant array for efficiency
                    my_arc.ringmap(knob_n, #[
                        15, 15, 15, 15, 15, 15, 15, 15,
                        15, 15, 15, 15, 15, 15, 15, 15,
                        15, 15, 15, 15, 15, 15, 15, 15,
                        15, 15, 15, 15, 15, 15, 15, 15,
                        15, 15, 15, 15, 15, 15, 15, 15,
                        15, 15, 15, 15, 15, 15, 15, 15,
                        15, 15, 15, 15, 15, 15, 15, 15,
                        15, 15, 15, 15, 15, 15, 15, 15 ]);

                    my_gathered_delta = 0;

                    ^ my_lead_led_position;
                };

            };
        };

        if(delta.isNegative)
        {
            if(my_lead_led_position == 0)
            {
                my_gathered_delta = 0;
                my_lead_led_position = 0;

                // using constant array for efficiency
                my_arc.ringmap(knob_n, #[ 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0, 0, 0, 0, 0, 0 ]);

                ^ my_lead_led_position;
            };


            if(my_lead_led_position > 0)
            {
                my_gathered_delta = my_gathered_delta + delta.abs;

                while{ (my_gathered_delta >= sensitvity) && (my_lead_led_position >= 0) }
                {
                    my_arc_map[my_lead_led_position] = 0;
                    my_lead_led_position = my_lead_led_position - 1;
                    my_gathered_delta = my_gathered_delta - sensitvity;
                };

                my_arc.ringmap(knob_n, my_arc_map);
            };

        };

        ^my_lead_led_position;
    }

    focus { arg knob_n;

        my_arc.ringmap(knob_n, my_arc_map);
    }

}