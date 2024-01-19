from typing import List
from rich.console import Console, OverflowMethod

console = Console(width=24, record=True)
supercali = "at java.base/java.lang.Thread.run(Thread.java:1583)"

overflow_methods: List[OverflowMethod] = ["fold", "crop", "ellipsis"]
for overflow in overflow_methods:
    console.rule(overflow)
    console.print(supercali, overflow=overflow, style="bold blue")
    console.print()

console.save_svg("example.svg", title=" . . . . . . . ")
