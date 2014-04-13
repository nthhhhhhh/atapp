/*

Makes the top half of a monome 64 behave switches for an arc 2.
Bottom half are just push buttons. Press is on, release off.
Classes is intended for atapp (another traktor app) but can be used
and modified for other generic purposes.

for information about monome devices:
monome.org

written by:
joseph rangel
https://github.com/nthhisst

*/

Plexer {

    var grid_64;                           // user's 64
    var arc_2;                             // user's arc 2
    var quadrant_I_last_press;             // top right section of 64
    var quadrant_II_last_press;            // top left section of 64
    var old_event_time_QI;                 // time stamp for last press in Q1
    var old_event_time_QII;                // time stamp for last press in Q2
    var switch;                            // switch 2D array representing buttons in Q1 and Q2
    var arc_scale_factor;                  // make sure leftmost knob sitting on top of grid is knob 0
    var midi_out;                          // MIDIOut object
    var midi_ctrl_nums_knob;               // MIDI control numbers for grid use for each respective switch (is a 2D array)
    var toggle_state;                      // on/off state to send via midi (to make button toggles)

    var <>sensitivity;                     // set accessor methods for sesntivity (array) ** not implemented correctly yet **


    *new { | your_grid, your_arc |

        ^super.new.initPlexer(your_grid, your_arc);

    }

    initPlexer{ | your_grid, your_arc |
        // for assigning midi control numbers for each knob at some switch
        var ctrl_number;

        /* all buttons on bottom quadrant have a potential state.
           not all will be utilitized but will be available for future
           modifications. */

        // initialize monome devices
        grid_64 = your_grid;
        arc_2 = your_arc;

        // turn off any previously lit leds
        arc_2.ringall(0, 0);
        arc_2.ringall(1, 0);
        grid_64.ledall(0);

        // set top quadrants initial positions
        grid_64.ledset(3,0,1);
        grid_64.ledset(4,0,1);

        // at launch, buttons: (4,0) and (3,0) are lit
        // set them as the last presses in their respective quadrant
        quadrant_I_last_press = [4, 0];
        quadrant_II_last_press = [3, 0];

        // time is going to be measured in beats
        old_event_time_QI = old_event_time_QII = SystemClock.beats;

        // arc sensitivity per switch
        // sensitivity = Array2D.new(4, 8);
        sensitivity = Array2D.fromArray(4, 8, Array.fill(32, 8));

        // create 2D array of togglestates for entire bottom quadrant of grid.
        // then initialize them all to zero.
        toggle_state = Array2D.fromArray(4, 8, Array.fill(32, 0));


        // ------------------ give each switch it's own knob ------------------

        /*
        Each switch[row][column] is proportional to the top quadrants of the grid.

        for example:
        switch[2][1] refers to the button (2,1) on the grid.

        Each switch contains a certain type of Arc-knob object and will
        change the behavior of the arc on the respective knob on press.
        Remember, the quadrants adhere to that cartesian system. The bottom
        half comprise of quadrants III and IV but are not distinguished.
        */ switch = Array2D.new(4, 8);

        // from all (0,0) to (2,7) add traktor style knobs
        for(0, 7, { |column|
            for(0, 2, { |row|
                switch[row, column] = ArcRotaryTraktor(arc_2, 4);
            });
        });

        // from (3,0) to (3,2) add encoder style knobs
        for(0,2, { |column|
            switch[3, column] = ArcEncoder(arc_2, 4);
        });

        // two traktor style knobs in the center
        switch[3,3] = ArcRotaryTraktor(arc_2, 4);
        switch[3,4] = ArcRotaryTraktor(arc_2, 4);

        // make the rest encoder style
        for(5, 7, { |column|
            switch[3, column] = ArcEncoder(arc_2, 16);
        });

        // end -----------------------------------------------------------------


        // --------------------------- setup MIDI ------------------------------

        // control number assignments begin at 40
        ctrl_number = 40;

        MIDIClient.init(1,1);

        midi_out = MIDIOut(0);
        midi_out.latency_(0);

        midi_ctrl_nums_knob = Array2D.new(4, 8);

        for(0, 7, { |column|
            for(0, 3, { |row|
                midi_ctrl_nums_knob[row, column] = ctrl_number;
                ctrl_number = ctrl_number + 1;
            });
        });

        // end ------------------------------------------------------------------

        // for 180˚ rotation
        arc_scale_factor = 0;

        if(arc_2.rot == 180)
        {
            arc_scale_factor = 1;
        };
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
            if( (new_event_time - old_event_time_QII) > 0.15 )
            {
                if(button_in_YX != quadrant_II_last_press)
                {
                    grid_64.ledset(quadrant_II_last_press[0], quadrant_II_last_press[1], 0);
                    grid_64.ledset(button_in_YX[0], button_in_YX[1], 1);
                    quadrant_II_last_press = button_in_YX;
                };

                old_event_time_QII = SystemClock.beats;

            };

            switch.at(quadrant_II_last_press[1], quadrant_II_last_press[0]).focusKnob(
                (0 + arc_scale_factor).wrap(0,1));
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

            switch.at(quadrant_I_last_press[1], quadrant_I_last_press[0]).focusKnob(
                (1 + arc_scale_factor).wrap(0,1));

        }

        // if bottom half
        { (column < 7) and: (row >= 4) and: (row <= 7) }
        {
            if ( ((row == 7) and: (column == 0)) || ((row == 7) and: (column == 4)) )
            {
                // to make an button a switch
                /*if(state != last_state){
                    if(state == 1){
                        push_counter = push_counter + 1;
                        midi_out.control(1, (column+row), 1);
                        grid_64.ledset(column, row, 1);
                    };
                };

                last_state = state;

                if(push_counter % 2 == 0){
                    midi_out.control(1, (column+row), 1);
                    grid_64.ledset(column, row, 1);
                } {
                    midi_out.control(1, (column+row), 0);
                    grid_64.ledset(column, row, 0);
                };*/

                if( (row == 7) and: (column == 0) ){

                };


                ^ button_in_YX
            };

            midi_out.control(1, (column+row), state);
            grid_64.ledset(column, row, state);
        };

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
            //quadrant_II_last_press.postln;
            // example: midi_out.control(chan: 1, ctlNum: 7, val: 64);

            if( arc_2.rot == 180 )
            {
                ^ midi_out.control(0,
                    midi_ctrl_nums_knob.at(quadrant_I_last_press[1], quadrant_I_last_press[0]),
                    switch.at(quadrant_I_last_press[1],
                        quadrant_I_last_press[0]).spin(knob_n, delta).linlin(0, 63, 0, 127).asInteger;
                );

            };
            ^ midi_out.control(0,
                midi_ctrl_nums_knob.at(quadrant_II_last_press[1], quadrant_II_last_press[0]),
                switch.at(quadrant_II_last_press[1],
                    quadrant_II_last_press[0]).spin(knob_n, delta).linlin(0, 63, 0, 127).asInteger
            );
        };

        if(knob_n == 1)
        {
            if( arc_2.rot == 180 )
            {
                ^ midi_out.control(0,
                    midi_ctrl_nums_knob.at(quadrant_II_last_press[1], quadrant_II_last_press[0]),
                    switch.at(quadrant_II_last_press[1],
                        quadrant_II_last_press[0]).spin(knob_n, delta).linlin(0, 63, 0, 127).asInteger);
            };
            ^ midi_out.control(0,
                midi_ctrl_nums_knob.at(quadrant_I_last_press[1], quadrant_I_last_press[0]),
                switch.at(quadrant_I_last_press[1],
                    quadrant_I_last_press[0]).spin(knob_n, delta).linlin(0, 63, 0, 127).asInteger);
        }
    }

}

