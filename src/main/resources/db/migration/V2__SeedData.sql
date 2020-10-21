-- J, first entity
insert into entities (name, summary, script, type)
values ('J', 'J is someone who has helped me once, when it mattered. If you are struggling, they will guide you through a brief mindfulness meditation to help you relax.','Hi, my name is J.
[Pause.]
How are you today?
[Pause.]
I would like to walk you through a brief exercise to help you relax.', 'Friend');

insert into entities (name, summary, script, type)
values ('Leaves on a Stream', 'This is a brief mindfulness meditation exercise to help you relax. It involves visualizing a stream of water and helps you let go of thoughts that might be stressful or overwhelming.',
'I invite you to sit in a comfortable yet upright position in your chair
with your feet flat on the floor,
your arms and legs uncrossed,
and your hands resting in your lap.
[Pause 3 seconds.]
Let your eyes gently close,
or fix them on a point in front of you.
[Pause 5 seconds.]
Take a couple of gentle breaths in...
[Pause 1 second]
... and out.
[Pause 1 second.]
Notice the sound and feel of your own breath
as you breathe in...
[Pause one second]
... and out.
[Pause 5 seconds.]
Now, I’d like you to imagine that you are standing by the shore
of a gently flowing stream and you are watching the water flow.
[Pause 3 seconds.]
Imagine feeling the ground beneath you,
the sounds of the water flowing past,
and the way the stream looks as you watch it.
[Pause 5 seconds.]
Imagine that there are leaves from trees,
of all different shapes, sizes, and colors,
floating past on the stream...
and you are just watching these float on the stream.
This is all you need to do for the time being.
[Pause 5 seconds.]
Start to become aware of your thoughts, feelings, or sensations.
[Pause 3 seconds.]
Each time you notice a thought, feeling, or sensation,
imagine placing it on a leaf and letting it float down the stream.
[Pause 5 seconds.]
Do this regardless of whether the thoughts, feelings, or sensations
are positive or negative, pleasurable or painful.
[Pause 3 seconds.]
Even if they are the most wonderful thoughts,
place them on a leaf and let them float by.
[Pause 5 seconds.]
If your thoughts stop, just watch the stream.
Sooner or later your thoughts should start up again.
[Pause 5 seconds.]
Allow the stream to flow at its own rate.
[Pause 3 seconds.]
Notice any urges to speed up or slow down the stream,
and let these be on leaves as well.
Observe the stream flow on its own and let it be.
[Pause 5 seconds.]
If you have thoughts, feelings, or sensations about doing this exercise,
place these on leaves as well.
[Pause 5 seconds.]
If a leaf gets stuck or will not go away, let it hang around.
For a little while, all you are doing is observing this experience;
there is no need to force the leaf down the stream.
[Pause 5 seconds.]
If you find yourself getting caught up with a thought or feeling,
such as boredom or impatience,
simply acknowledge it.
Say to yourself, “Here’s a feeling of boredom,”
or “Here’s a feeling of impatience.”
Then place those words on a leaf,
and let them float on by.
[Pause five seconds.]
You are just observing each experience and placing it on a leaf on the stream.
It is normal and natural to lose track of this exercise, and it will keep happening.
When you notice yourself losing track,
just bring yourself back to watching the leaves on the stream.
[Pause ten seconds.]
Notice the stream, and place any thoughts, feelings, or sensations on the leaves
and let them gently float down the stream.
[Pause five seconds.]
Finally, allow the image of the stream to dissolve,
and slowly bring your attention back to sitting in the chair,
in this room.
[Pause.]
Gently open your eyes and notice what you can see.
Notice what you can hear.
Bring your awareness to the present moment.
Push your feet into the floor and have a stretch.
Notice yourself stretching.
Notice yourself taking a deep breath.
[Pause.]
Welcome back.', 'Meditation');

insert into entity_relationships (primary_entity_id, target_entity_id, type) values (1, 2, 'FriendHasMeditation');