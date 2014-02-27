(
s.waitForBoot({

    // create monome devices
    ~sixtyFour = MonoM.new("/sixtyFour", 180);
    ~arcTwo = Arc.new("/arcTwo", 180);

    // the monmome and arc class do not destinguish
    // your device index, use 'yourDevice'.dvcnum.postln
    // to find which ones are at which index.
    // if you find the application is not working
    // it is most likely an issue with device indexing and selection.
    ~sixtyFour.useDevice(0);
    ~arcTwo.useDevice(1);

    // Plexer class takes one grid object and one arc object.
    ~pplexer = Plexer.new(~sixtyFour, ~arcTwo);

    // grid stream
    OSCFunc.newMatching({ | message |

        // note: message[1] represents columns, message[2] represents rows, message[3] represents state
        ~pplexer.streamPresses(message[1], message[2], message[3]);

    }, "/sixtyFour/grid/key"); // make sure this line matches with the prefix you defined when creating a MonoM object

    // arc stream
    OSCFunc.newMatching({ | message |

        // note: message[1] represents knob_n, message[2] represents delta
        ~pplexer.streamRotation(message[1], message[2]);

    }, "/arcTwo/enc/delta"); // make sure this line matches with the prefix you defined when creating a Arc object

});

)

(
~arcTwo.darkness;
~sixtyFour.darkness;
s.quit;
)


/*

only 0˚ and 180˚ rotation with arcs are functional with atapp.
though, you may rotate your grid as you wish.
it is confusing for further implementation because i simply
don't know where a user will move their arc.

simply, if the arc is resting above or below a grid,
the leftmost knob will match with the top left of your grid.
the rightmost knob will match with the top right of your grid.

i may implement 90˚ and 270˚ rotation for arc in the future
if i find a comfortable setup.

if you plan on using internal midi routing, be sure to setup a virtual
midi port. on os x this is will be "audio midi setup" in /Applications/Utilities.

doing a google search of "setting up IAC midi osx" yields some easy setup advice.

*/