(
s.waitForBoot({

    ~arcTwo = Arc.new("/arcTwo", 0);
    ~sixtyFour = MonoM.new("/sixtyFour", 0);

    ~sixtyFour.useDevice(0);
    ~arcTwo.useDevice(1);

    ~pplexer = Plexer.new(~sixtyFour, ~arcTwo);

   q =  OSCFunc.newMatching({ | message |

        // note: message[1] represents columns, message[2] represents rows, message[3] represents state
        ~pplexer.streamPresses(message[1], message[2], message[3]);

    }, "/sixtyFour/grid/key");

   r = OSCFunc.newMatching({ | message |

        // note: message[1] represents knob_n, message[2] represents delta
        ~pplexer.streamRotation(message[1], message[2]);

    }, "/arcTwo/enc/delta");

});

)

(
~arcTwo.darkness;
~sixtyFour.darkness;
s.quit;
)
