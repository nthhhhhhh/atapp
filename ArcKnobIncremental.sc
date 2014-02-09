/*

ArcKnobIncremental lights one led as per position.
Returns -1 if rotation is negative and 1 for positive
rotation.

https://github.com/nthhisst


*/

ArcKnobIncremental{

	var my_arc,
	<>sensitivity,
	my_arc_map,
	my_current_led,
	my_gathered_delta;

	*new{ | arc, sensitivity_level |

		^super.new.initArcKnob(arc, sensitivity_level);
	}

	initArcKnob { | arc, sensitivity_level |

		my_arc = arc;
		sensitivity = sensitivity_level;

		my_arc_map = Array.fill(64, 0);

		my_arc_map[0] = 15;

		my_current_led = 0;
		my_gathered_delta = 0;

	}

	spin { | knob_n, delta |

		if(delta.isStrictlyPositive)
		{
			my_gathered_delta = my_gathered_delta + delta;

			while {my_gathered_delta >= sensitivity}
			{
				my_arc_map[my_current_led] = 0;
				my_current_led = my_current_led + 1;
				my_current_led = my_current_led.wrap(0, 63);
				my_arc_map[my_current_led] = 15;
				my_gathered_delta = my_gathered_delta - sensitivity;
			};

			my_arc.ringmap(knob_n, my_arc_map);
		}

		{ // if it's negative

			my_gathered_delta = my_gathered_delta + delta.abs;

			while {my_gathered_delta >= sensitivity}
			{
				my_arc_map[my_current_led] = 0;
				my_current_led = my_current_led - 1;
				my_current_led = my_current_led.wrap(0, 63);
				my_arc_map[my_current_led] = 15;
				my_gathered_delta = my_gathered_delta - sensitivity;
			};

			my_arc.ringmap(knob_n, my_arc_map);
		};

		^delta.sign;
	}

	focus { | knob_n |

		my_arc.ringmap(knob_n, my_arc_map);

	}

}