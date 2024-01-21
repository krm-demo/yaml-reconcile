import unicodedata

def dump_range(code_point_from, code_point_to):
    for c in range(code_point_from, code_point_to + 1):
        esc_u = f"'\\U{c:08x}'"
        esc_x = "".join([f"\\x{b:02x}" for b in chr(c).encode("utf-8")])
        ctg = unicodedata.category(chr(c))
        name = unicodedata.name(chr(c))
        print(f"[{chr(c)}] - {esc_u} - '{esc_x}' --> {ctg}, {name}")


print("--- 'Box Drawing' https://www.compart.com/en/unicode/block/U+2500 ------")
print("from: https://www.compart.com/en/unicode/U+2500")
print("  to: https://www.compart.com/en/unicode/U+257F")
dump_range(0x2500, 0x257f)
print()

print("--- 'Block Elements' https://www.compart.com/en/unicode/block/U+2580 ---")
print("from: https://www.compart.com/en/unicode/U+2580")
print("  to: https://www.compart.com/en/unicode/U+259F")
dump_range(0x2580, 0x259f)
print()
print(f'... > echo -e "\\xe2\\x96\\x9e\\xe2\\x96\\x9a\\x0a\\xe2\\x96\\x80\\xe2\\x96\\x80"')
print("\u259e\u259a\n\u2580\u2580")
print("\U0000259e\U0000259a\n\U0000259c\U0000259b")
print()

print("--- 'Miscellaneous Symbols and Pictographs' https://www.compart.com/en/unicode/block/U+1F300 ---")
print("from: https://www.compart.com/en/unicode/U+1f300")
print("  to: https://www.compart.com/en/unicode/U+1f5ff")
dump_range(0x1f300, 0x1f5ff)
print()

print("--- 'Emoticons' https://www.compart.com/en/unicode/block/U+1F600 ---")
print("from: https://www.compart.com/en/unicode/U+1F600")
print("  to: https://www.compart.com/en/unicode/U+1f64F")
dump_range(0x1F600, 0x1f64F)
print()
