-- J, first entity
insert into entities (entity_name, summary, script, type)
values ('J', 'J is someone who has helped me once, when it mattered. If you are struggling, they will guide you through a brief mindfulness meditation to help you relax.','Hi, my name is J.\n\n[Pause.]\n\nHow are you today?\n\n[Pause.]\n\nI would like to walk you through a brief exercise to help you relax.', 'Friend');

insert into entities (entity_name, summary, script, type)
values ('Leaves on a Stream', 'This is a brief mindfulness meditation exercise to help you relax. It involves visualizing a stream of water and helps you let go of thoughts that might be stressful or overwhelming.',
'I invite you to sit in a comfortable yet upright position in your chair\nwith your feet flat on the floor,\nyour arms and legs uncrossed,\nand your hands resting in your lap.\n[Pause 3 seconds.]\nLet your eyes gently close,\nor fix them on a point in front of you.\n[Pause 5 seconds.]\nTake a couple of gentle breaths in...\n[Pause 1 second]\n... and out.\n[Pause 1 second.]\nNotice the sound and feel of your own breath\nas you breathe in...\n[Pause one second]\n... and out.\n[Pause 5 seconds.]\nNow, I’d like you to imagine that you are standing by the shore\nof a gently flowing stream and you are watching the water flow.\n[Pause 3 seconds.]\nImagine feeling the ground beneath you,\nthe sounds of the water flowing past,\nand the way the stream looks as you watch it.\n[Pause 5 seconds.]\nImagine that there are leaves from trees,\nof all different shapes, sizes, and colors,\nfloating past on the stream...\nand you are just watching these float on the stream.\nThis is all you need to do for the time being.\n[Pause 5 seconds.]\nStart to become aware of your thoughts, feelings, or sensations.\n[Pause 3 seconds.]\nEach time you notice a thought, feeling, or sensation,\nimagine placing it on a leaf and letting it float down the stream.\n[Pause 5 seconds.]\nDo this regardless of whether the thoughts, feelings, or sensations\nare positive or negative, pleasurable or painful.\n[Pause 3 seconds.]\nEven if they are the most wonderful thoughts,\nplace them on a leaf and let them float by.\n[Pause 5 seconds.]\nIf your thoughts stop, just watch the stream.\nSooner or later your thoughts should start up again.\n[Pause 5 seconds.]\nAllow the stream to flow at its own rate.\n[Pause 3 seconds.]\nNotice any urges to speed up or slow down the stream,\nand let these be on leaves as well.\nObserve the stream flow on its own and let it be.\n[Pause 5 seconds.]\nIf you have thoughts, feelings, or sensations about doing this exercise,\nplace these on leaves as well.\n[Pause 5 seconds.]\nIf a leaf gets stuck or will not go away, let it hang around.\nFor a little while, all you are doing is observing this experience;\nthere is no need to force the leaf down the stream.\n[Pause 5 seconds.]\nIf you find yourself getting caught up with a thought or feeling,\nsuch as boredom or impatience,\nsimply acknowledge it.\nSay to yourself, “Here’s a feeling of boredom,”\nor “Here’s a feeling of impatience.”\nThen place those words on a leaf,\nand let them float on by.\n[Pause five seconds.]\nYou are just observing each experience and placing it on a leaf on the stream.\nIt is normal and natural to lose track of this exercise, and it will keep happening.\nWhen you notice yourself losing track,\njust bring yourself back to watching the leaves on the stream.\n[Pause ten seconds.]\nNotice the stream, and place any thoughts, feelings, or sensations on the leaves\nand let them gently float down the stream.\n[Pause five seconds.]\nFinally, allow the image of the stream to dissolve,\nand slowly bring your attention back to sitting in the chair,\nin this room.\n[Pause.]\nGently open your eyes and notice what you can see.\nNotice what you can hear.\nBring your awareness to the present moment.\nPush your feet into the floor and have a stretch.\nNotice yourself stretching.\nNotice yourself taking a deep breath.\n[Pause.]\nWelcome back.', 'Meditation');

insert into entity_relationships (primary_entity_id, target_entity_id, type) values (1, 2, 'FriendHasMeditation');