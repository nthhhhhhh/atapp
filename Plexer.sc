Plexer {

    var grid_64;
    var arc;
    var quadrant_I_last_press;
    var quadrant_II_last_press;
    var old_event_time_QI;
    var old_event_time_QII;
    var arcRouter;

    *new { | your_grid |

        ^super.new.initPlexer(your_grid);

    }

    initPlexer{ | your_grid |

        grid_64 = your_grid;
        quadrant_I_last_press = [4, 0];
        quadrant_II_last_press = [3, 0];
        old_event_time_QI = old_event_time_QII = SystemClock.beats;

        grid_64.ledall(0);
        grid_64.ledset(3,0,1);
        grid_64.ledset(4,0,1);

        arcRouter = { | column, row |

            case
            { (column == 0) and: (row == 0) }
            { }

            { (column == 0) and: (row == 1) }
            { }

            { (column == 0) and: (row == 2) }
            { }

            { (column == 0) and: (row == 3) }
            { }

            { (column == 1) and: (row == 0) }
            { }

            { (column == 1) and: (row == 1) }
            { }

            { (column == 1) and: (row == 2) }
            { }

            { (column == 1) and: (row == 3) }
            { }

            { (column == 2) and: (row == 0) }
            { }

            { (column == 2) and: (row == 1) }
            { }

            { (column == 2) and: (row == 2) }
            { }

            { (column == 2) and: (row == 3) }
            { }

            { (column == 3) and: (row == 0) }
            { }

            { (column == 3) and: (row == 1) }
            { }

            { (column == 3) and: (row == 2) }
            { }

            { (column == 3) and: (row == 3) }
            { }

            { (column == 4) and: (row == 0) }
            { }

            { (column == 4) and: (row == 1) }
            { }

            { (column == 4) and: (row == 2) }
            { }

            { (column == 4) and: (row == 3) }
            { }

            { (column == 5) and: (row == 0) }
            { }

            { (column == 5) and: (row == 1) }
            { }

            { (column == 5) and: (row == 2) }
            { }

            { (column == 5) and: (row == 3) }
            { }

            { (column == 6) and: (row == 0) }
            { }

            { (column == 6) and: (row == 1) }
            { }

            { (column == 6) and: (row == 2) }
            { }

            { (column == 6) and: (row == 3) }
            { }

            { (column == 7) and: (row == 0) }
            { }

            { (column == 7) and: (row == 1) }
            { }

            { (column == 7) and: (row == 2) }
            { }

            { (column == 7) and: (row == 3) }
            { }


        }


    }

    streamPresses { | column, row, state |

        var new_event_time = SystemClock.beats;
        var button_in_YX = [column, row];

        // if top left corner
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

        // if top right corner
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


}