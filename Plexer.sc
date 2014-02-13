/*

Makes the top half of a monome 64 behave switches for an arc 2.
Bottom half are just push buttons. Press is on, release off.
Classes is intended for atapp (another traktor app) but can be used
and modified for other generic purposes. throughout this class the
the coordinates are as followed in comments:
( column, row )

for information about monome devices:
monome.org

written by:
joseph rangel
https://github.com/nthhisst

*/

Plexer {

    // variables
    var grid_64;
    var arc_2;
    var quadrant_I_last_press;
    var quadrant_II_last_press;
    var old_event_time_QI;
    var old_event_time_QII;
    var switch;

    // accessors
    var <>sensitivty;

    *new { | your_grid, your_arc |

        ^super.new.initPlexer(your_grid, your_arc);

    }

    initPlexer{ | your_grid, your_arc |

        grid_64 = your_grid;
        arc_2 = your_arc;
        quadrant_I_last_press = [4, 0];
        quadrant_II_last_press = [3, 0];
        old_event_time_QI = old_event_time_QII = SystemClock.beats;

        // turn off any previously lit leds and
        arc_2.ringall(0, 0);
        arc_2.ringall(1, 0);
        grid_64.ledall(0);
        grid_64.ledset(3,0,1);
        grid_64.ledset(4,0,1);

        // arc sensitivity per switch
        sensitivity = Array2D.new(4, 8);

        // default sensitivty is 4 but can be changed by user
        for(0, 7, { arg column;

            for(0, 3, { arg row;
                sensitivity[row, column] = 4;
            });

        });



        // ------------------ give each switch it's own knob ------------------

        switch = Array2D.new(4, 8);

        // from all (0,0) to (2,7) add Absolute knobs
        for(0, 7, { arg column;

            for(0, 2, { arg row;
                switch[row, column] = ArcKnobAbsolute.new(arc_2, 4);
            });

        });



        // from (3,0) to (3,2) add Incremental knobs
        for(0,2, { arg column;
            switch[3, column] = ArcKnobIncremental.new(arc_2, 4);
        });

        // two Absolute knobs in the center
        switch[3,3] = ArcKnobAbsolute.new(arc_2, 4);
        switch[3,4] = ArcKnobAbsolute.new(arc_2, 4);

        // make the rest incremental
        for(5, 7, { arg column;
            switch[3, column] = ArcKnobIncremental(arc_2, 4);
        });

        // ---------------------------------------------------------------------



        /*        arcRouter = { | column, row, knob_n |

        switch[row, column].focus;

        ^ switch[row, column].spin(knob_n, sensitivity[row, column]);

        }*/


    }

    /*
    Prevents multiple button presses in quadrant I or II.
    Only one LED may be lit in either quadrant. Also returns which
    button was pressed last in either quadrant.
    */
    streamPresses { | column, row, state |

        var new_event_time = SystemClock.beats;
        var button_in_YX = [column, row];

        // if top left corner (Quadrant II)
        case { (column <= 3) and: (row <= 3) }
        {
            if( (new_event_time - old_event_time_QII) > 0.2 )
            {
                if(button_in_YX != quadrant_II_last_press)
                {
                    grid_64.ledset(quadrant_II_last_press[0], quadrant_II_last_press[1], 0);
                    grid_64.ledset(button_in_YX[0], button_in_YX[1], 1);
                    quadrant_II_last_press = button_in_YX;
                };

                old_event_time_QII = SystemClock.beats;

            };
        }

        // if top right corner (Quadrant I)
        { (column <= 7) and: (column >= 4) and: (row <= 3) }
        {
            if((new_event_time - old_event_time_QI) > 0.2)
            {
                if(button_in_YX != quadrant_I_last_press)
                {
                    grid_64.ledset(quadrant_I_last_press[0], quadrant_I_last_press[1], 0);
                    grid_64.ledset(button_in_YX[0], button_in_YX[1], 1);
                    quadrant_I_last_press = button_in_YX;
                };

                old_event_time_QI = SystemClock.beats;
            };

        }

        // if bottom half
        { (column < 7) and: (row >= 4) and: (row <= 7) }
        {
            grid_64.ledset(column, row, state);
        }

        ^button_in_YX;
    }

    /*
    Assigns button press in respective quadrant to specific arcKnob.
    Hence, plexer => multiplexer.
    */
    streamRotation{ | knob_n, delta |

        // quadrant_N_last_press[1] refers to x (row) coordinate on grid
        // quadrant_N_last_press[0] refers to y (column) coordinate on grid

        if(knob_n == 0)
        {
            switch[quadrant_I_last_press[1], quadrant_I_last_press[0]].focus;
            switch[quadrant_I_last_press[1], quadrant_I_last_press[0]].spin(knob_n, delta);
        };

        if(knob_n == 1)
        {
            switch[quadrant_II_last_press[1], quadrant_II_last_press[0]].focus;
            switch[quadrant_II_last_press[1], quadrant_II_last_press[0]].spin(knob_n, delta);
        }

    }

}