from main import BlizzardNavigator


def test_simple():
    blizzard_navigator, start, finish = BlizzardNavigator.parse('''
    #.#####
    #.....#
    #>....#
    #.....#
    #...v.#
    #.....#
    #####.#    
    ''')
    assert blizzard_navigator.get_path_length(start, finish) == 10


def test_complex():
    blizzard_navigator, start, finish = BlizzardNavigator.parse('''
    #.######
    #>>.<^<#
    #.<..<<#
    #>v.><>#
    #<^v^^>#
    ######.#    
    ''')
    assert blizzard_navigator.get_path_length(start, finish) == 18
