//
//  ILQuestionFormViewController.m
//  Inner Life
//
//  Created by Matthew Bates on 26/07/2013.
//  Copyright (c) 2013 Matthew Bates. All rights reserved.
//

#import "ILQuestionFormViewController.h"
#import "common.h"

@interface ILQuestionFormViewController ()
{
    NSArray *sections;
}
@end

@implementation ILQuestionFormViewController

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self)
    {
        data = [ILDataSingleton instance];
        data.currentQuestion = 0;
        data.currentSection  = 9;
        
        _currentSession = 1;
        sections = [data getQuestionsInSectionsFilteredBySession:_currentSession];
        
        data.responses = [[NSMutableDictionary alloc] init];
    }
    return self;
}

- (void)loadView
{    
    UIView *view = [[UIView alloc] initWithFrame:[[UIScreen mainScreen] applicationFrame]];
    [view setBackgroundColor:[UIColor whiteColor]];
    
    CGFloat width = view.frame.size.width;
    CGFloat height = view.frame.size.height - NAVIGATION_BAR_HEIGHT;
    
    _questionProgressBar = [[ILQuestionProgressBar alloc] initWithFrame:CGRectMake(0, height - 44, width, 44)];
    [_questionProgressBar setBackgroundColor:[UIColor whiteColor]];
    
    UIButton *previousButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [previousButton addTarget:self action:@selector(previousButton:) forControlEvents:UIControlEventTouchUpInside];
    [previousButton setTitle:@"Previous" forState:UIControlStateNormal];
    previousButton.frame = CGRectMake(5, height - _questionProgressBar.frame.size.height - 2 - 44, width/2 - 8, 44);
    
    UIButton *nextButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
    [nextButton addTarget:self action:@selector(nextButton:) forControlEvents:UIControlEventTouchUpInside];
    [nextButton setTitle:@"Next" forState:UIControlStateNormal];
    nextButton.frame = CGRectMake(previousButton.frame.origin.x + previousButton.frame.size.width + 6,
                                  height - _questionProgressBar.frame.size.height - 2 - 44, width/2 - 8, 44);

    
    _tableView = [[UITableView alloc] initWithFrame:CGRectMake(0, 0, width, previousButton.frame.origin.y -5) style:UITableViewStyleGrouped];
    _tableView.dataSource = self;
    _tableView.delegate = self;
    
    [view addSubview:_tableView];
    [view addSubview:previousButton];
    [view addSubview:nextButton];
    [view addSubview:_questionProgressBar];
    self.view = view;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.title = @"Questions";
    
    NSMutableArray *sectionLengthSpec = [[NSMutableArray alloc] initWithCapacity:[sections count]];
    for (ILSection *section in sections)
    {
        [sectionLengthSpec addObject:[NSNumber numberWithInteger:[section.questions count]]];
    }
    
    _questionProgressBar.sectionSpecification = sectionLengthSpec;
    _questionProgressBar.currentSection = 0;
    _questionProgressBar.currentSubSection = 0;
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

- (void)nextButton: (UIButton *)sender
{
    if (data.currentQuestion == ([[data getQuestionsBySection:data.currentSection] count] -1) && data.currentSection == 3)
    {
        NSLog(@"FINISH");
    }
    
    if (data.currentQuestion == ([[data getQuestionsBySection:data.currentSection] count] -1))
    {
        data.currentSection++;
        data.currentQuestion = 0;
    }
    else
    {
        data.currentQuestion++;
    }
    
    _questionProgressBar.currentSubSection++;
    
    [self.tableView reloadData];
}

- (void)previousButton: (UIButton *)sender
{
    if (data.currentSection == 0 && data.currentQuestion == 0)
    {
        return;
    }
    
    data.currentQuestion--;
    if (data.currentQuestion < 0)
    {
        data.currentSection = (data.currentSection > 0) ? (data.currentSection - 1) : 0;
        data.currentQuestion = [[data getQuestionsBySection:data.currentSection] count] -1;
        
        if (data.currentSection < 0)
        {
            data.currentSection = 0;
        }
    }
    
    _questionProgressBar.currentSubSection--;
    
    [self.tableView reloadData];
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 2;
}

- (NSInteger)tableView:(UITableView *)tableView numberOfRowsInSection:(NSInteger)section
{
    if (section == 1)
    {
        NSArray *choices = [self getCurrentSection].choices ;
        return [choices count];
    }
    else
    {
        return 1;
    }
}

- (ILSection *)getCurrentSection
{
    ILSection *section = [[[ILDataSingleton instance] getQuestionsInSections] objectAtIndex:[ILDataSingleton instance].currentSection];
    return section;
}

- (ILQuestion *)getCurrentQuestion:(ILSection *)section
{
    ILQuestion *question = [section.questions objectAtIndex:[ILDataSingleton instance].currentQuestion];
    return question;
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath
{
    ILSection *section = [self getCurrentSection];
    ILQuestion *question = [self getCurrentQuestion:section];
    
    if (indexPath.section == 0)
    {
        return MAX(([question.questionDescription sizeWithFont:[UIFont italicSystemFontOfSize:15] constrainedToSize:CGSizeMake(280, 9000) lineBreakMode:NSLineBreakByWordWrapping].height) + 14, 44);
    }
    if (indexPath.section == 1)
    {
        ILChoice *choice = [section.choices objectAtIndex:indexPath.row];
        return MAX(([choice.text  sizeWithFont:[UIFont systemFontOfSize:15] constrainedToSize:CGSizeMake(280, 9000) lineBreakMode:NSLineBreakByWordWrapping].height) + 14, 44);
    }
    if (indexPath.section == 2)
    {
        return 50;
    }
    else return 44;
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    static NSString *TextCellIdentifier = @"TextCell";
    static NSString *ButtonCellIdentifier = @"ButtonCell";
    
    UITableViewCell *cell;
    
    if (indexPath.section == 2)
    {
        cell = [tableView dequeueReusableCellWithIdentifier:ButtonCellIdentifier];
    }
    else
    {
        cell = [tableView dequeueReusableCellWithIdentifier:TextCellIdentifier];
        
    }
    
    if (cell == nil)
    {
        if (indexPath.section == 2)
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:ButtonCellIdentifier];
        }
        else
        {
            cell = [[UITableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:TextCellIdentifier];
        }
    }
    
    cell.selectionStyle = UITableViewCellSelectionStyleNone;
    
    if (indexPath.section == 0)
    {
        cell.textLabel.font = [UIFont italicSystemFontOfSize:15];
        cell.textLabel.numberOfLines = 0;
        cell.textLabel.lineBreakMode = NSLineBreakByWordWrapping;
        cell.textLabel.text = [self getCurrentQuestion:[self getCurrentSection]].questionDescription;
        
    }
    else if (indexPath.section == 1)
    {
        ILChoice *choice = [[self getCurrentSection].choices objectAtIndex:indexPath.row];
        cell.textLabel.font = [UIFont systemFontOfSize:15];
        cell.textLabel.numberOfLines = 0;
        cell.textLabel.lineBreakMode = NSLineBreakByWordWrapping;
        cell.textLabel.text = choice.text;
        
        ILChoice *response = [data.responses objectForKey:[NSString stringWithFormat:@"%d", data.currentQuestion]];
        
        if (response)
        {
            if (response.value == choice.value)
            {
                cell.accessoryType = UITableViewCellAccessoryCheckmark;
            }
            else
            {
                cell.accessoryType = UITableViewCellAccessoryNone;
            }
            
        }
        else
        {
            cell.accessoryType = UITableViewCellAccessoryNone;
        }
        
    }
    else if (indexPath.section == 2)
    {
        UIButton* prevButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        [prevButton addTarget:self action:@selector(previousButton:) forControlEvents:UIControlEventTouchDown];
        [prevButton setTitle:@"Previous" forState:UIControlStateNormal];
        prevButton.frame = CGRectMake(13, 4, cell.frame.size.width/2.0 - 15, 44.0);
        [cell addSubview:prevButton];
        
        UIButton* nextButton = [UIButton buttonWithType:UIButtonTypeRoundedRect];
        [nextButton addTarget:self action:@selector(nextButton:) forControlEvents:UIControlEventTouchDown];
        [nextButton setTitle:@"Next" forState:UIControlStateNormal];
        nextButton.frame = CGRectMake(cell.frame.size.width/2.0 + 2, 4, cell.frame.size.width/2.0 - 14, 44.0);
        [cell addSubview:nextButton];
    }
    
    return cell;
}

#pragma mark - Table view delegate

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    ILChoice *choice = [[self getCurrentSection].choices objectAtIndex:indexPath.row];
    [data.responses setObject:choice forKey:[NSString stringWithFormat:@"%d", data.currentQuestion]];
    [self.tableView reloadData];
}

@end